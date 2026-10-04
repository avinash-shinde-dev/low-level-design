package com.shikavani.lld.librarymanagement;

import com.shikavani.lld.librarymanagement.decorator.*;
import com.shikavani.lld.librarymanagement.enums.*;
import com.shikavani.lld.librarymanagement.exception.*;
import com.shikavani.lld.librarymanagement.models.*;
import com.shikavani.lld.librarymanagement.models.FineBreakdown;
import com.shikavani.lld.librarymanagement.notification.InMemoryEventBus;
import com.shikavani.lld.librarymanagement.notification.Notification;
import com.shikavani.lld.librarymanagement.repository.*;
import com.shikavani.lld.librarymanagement.service.*;
import com.shikavani.lld.librarymanagement.strategy.deliver.DeliveryChannel;

import java.math.BigDecimal;
import java.time.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntConsumer;

public class LibraryManagementDemo {

    // =====================================================================================
    // 1. Tiny helpers for printing checks
    // =====================================================================================
    static int passed = 0, failed = 0;

    static void title(String text) { System.out.println("\n=== " + text + " ==="); }

    static void check(String what, boolean ok) {
        if (ok) passed++; else failed++;
        System.out.println("  [" + (ok ? "PASS" : "FAIL") + "] " + what);
    }

    /** The action must fail with the given exception type. The (clear) error message is printed. */
    static void expectError(String what, Class<? extends RuntimeException> type, Runnable action) {
        try {
            action.run();
            check(what + " -> expected " + type.getSimpleName() + " but it worked", false);
        } catch (RuntimeException e) {
            check(what + " -> rejected: \"" + e.getMessage() + "\"", type.isInstance(e));
        }
    }

    static boolean same(BigDecimal actual, String expected) { return actual.compareTo(new BigDecimal(expected)) == 0; }

    /** Starts 'threads' threads at the same moment and waits until all are done. */
    static void race(int threads, IntConsumer task) throws InterruptedException {
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        CountDownLatch ready = new CountDownLatch(threads), go = new CountDownLatch(1);
        for (int i = 0; i < threads; i++) {
            final int n = i;
            pool.submit(() -> {
                ready.countDown();
                try { go.await(); } catch (InterruptedException e) { return; }
                task.accept(n);
            });
        }
        ready.await();
        go.countDown();
        pool.shutdown();
        pool.awaitTermination(30, TimeUnit.SECONDS);
    }

    // =====================================================================================
    // 2. Membership tiers. All numbers are plain constructor arguments (nothing hard-coded in the services).
    // =====================================================================================
    static final BigDecimal FINE_LIMIT = new BigDecimal("500");   // borrowing is blocked above Rs 500 of unpaid fines

    //                                                     tier                max  days  fine x  limit       pickup hrs
    static Membership basic()   { return new Membership(MembershipTier.BASIC,   2,   7,    BigDecimal.ONE,        FINE_LIMIT, 48); }
    static Membership premium() { return new Membership(MembershipTier.PREMIUM, 5,   14,   new BigDecimal("0.5"), FINE_LIMIT, 48); }

    // =====================================================================================
    // 3. Test helpers: a clock we control, and notification channels we can inspect
    // =====================================================================================

    /** Time only moves when we say so, so "5 days late" needs no waiting. */
    static class MutableClock extends Clock {
        private volatile Instant now = Instant.parse("2026-01-05T09:00:00Z");
        @Override public ZoneId getZone() { return ZoneOffset.UTC; }
        @Override public Clock withZone(ZoneId zone) { return this; }
        @Override public Instant instant() { return now; }
        void advance(Duration d) { now = now.plus(d); }
        void setTo(LocalDateTime t) { now = t.toInstant(ZoneOffset.UTC); }
    }

    /** Email channel that remembers what it "sent". */
    static class RecordingChannel implements DeliveryChannel {
        final List<Notification> sent = new CopyOnWriteArrayList<>();
        @Override public ChannelType type() { return ChannelType.EMAIL; }
        @Override public void deliver(Notification n) { sent.add(n); }
        long count(String memberId, EventType type) {
            return sent.stream().filter(n -> n.recipient().getId().equals(memberId) && n.eventType() == type).count();
        }
    }

    /** SMS channel that is always down: proves a failing channel cannot break borrow / return. */
    static class BrokenChannel implements DeliveryChannel {
        final AtomicInteger attempts = new AtomicInteger();
        @Override public ChannelType type() { return ChannelType.SMS; }
        @Override public void deliver(Notification n) { attempts.incrementAndGet(); throw new DeliveryException("SMS gateway is down"); }
    }

    // =====================================================================================
    // 4. Library = the whole system wired together (a framework like Spring would normally do this)
    // =====================================================================================
    static class Library {
        final MutableClock clock = new MutableClock();
        final RecordingChannel email = new RecordingChannel();
        final BrokenChannel sms = new BrokenChannel();

        final MemberService memberService;
        final TransactionService transactionService;
        final HoldService holdService;
        final CatalogService catalogService;
        final BranchService branchService;
        final BorrowService borrowService;
        final ReturnService returnService;
        final FineCalculationService fineService;
        final SearchService searchService;
        final SchedulerService scheduler;
        final Branch branch1, branch2;

        Library() {
            BookCopyRepository copyRepo = new BookCopyRepository();
            InMemoryEventBus bus = new InMemoryEventBus();

            memberService = new MemberService(new MemberRepository());
            transactionService = new TransactionService(new TransactionRepository(), clock);
            holdService = new HoldService(new HoldRepository(), memberService, copyRepo, bus, clock);
            catalogService = new CatalogService(new CatalogRepository(), transactionService, holdService, copyRepo);
            branchService = new BranchService(new BranchRepository(), catalogService, copyRepo, holdService);
            borrowService = new BorrowService(branchService, memberService, catalogService, transactionService, holdService, clock);
            searchService = new SearchService(catalogService, copyRepo);

            // Fine rules, innermost first: Base (Rs10/day x tier multiplier) -> 2 free days -> Rs50 extra after 30 days -> capped at book price.
            // A new rule = one new class added to this line. No existing class changes.
            FineDecorator rules = new CapDecorator(new LongTermPenaltyDecorator(
                    new GracePeriodDecorator(new BaseFineDecorator(), 2), 30, new BigDecimal("50")));
            fineService = new FineCalculationService(memberService, catalogService, rules, new BigDecimal("10"));
            returnService = new ReturnService(branchService, holdService, fineService, transactionService, memberService, clock);

            // "Runnable::run" = deliver immediately (no background threads), so checks are predictable
            new NotificationService(List.of(email, sms), bus, Runnable::run);
            scheduler = new SchedulerService(transactionService, memberService, fineService, holdService, bus, clock, Duration.ofDays(2));

            branch1 = branchService.addBranch("Branch-1");
            branch2 = branchService.addBranch("Branch-2");
        }

        /** Every member wants email AND sms (sms is broken in this demo, on purpose). */
        Member newMember(String id, Membership membership) {
            Member m = new Member(id, id, membership, List.of(ChannelType.EMAIL, ChannelType.SMS));
            memberService.register(m);
            return m;
        }

        /** Adds a book to the catalog and 'copies' copies at Branch-1. */
        Book newBook(String title, String isbn, String author, Genre genre, String price, int copies) {
            Book book = new Book(title, isbn, Set.of(new Author(author)), Set.of(genre),
                    new BigDecimal(price), new Publisher("Gnome Press"), 1951);
            catalogService.addBook(book);
            if (copies > 0) branchService.addBookCopies(branch1.id(), book.getId(), copies);
            return book;
        }

        Transaction borrow(String memberId, Book book) {
            return borrowService.borrowBook(branch1.id(), memberId, book.getId());
        }

        List<String> queueOf(Book book) {
            return holdService.getWaitingHolds(book.getId()).stream().map(Hold::getMemberId).toList();
        }
    }

    // =====================================================================================
    // SCENARIOS
    // =====================================================================================

    /** Scenarios 1-4 are one story: borrow, queue, late return with fine, hold expiry. */
    static void holdQueueStory() {
        title("Scenarios 1-4: borrow, hold queue, late return with fine, hold expiry");
        Library lib = new Library();

        // --- Scenario 1: set-up. A = BASIC (limit 2), B and C = PREMIUM (limit 5). One book, ONE copy at Branch-1.
        lib.newMember("A", basic());
        lib.newMember("B", premium());
        lib.newMember("C", premium());
        Book book = lib.newBook("Foundation", "ISBN-1", "Isaac Asimov", Genre.SCI_FI, "600", 1);
        check("S1: one copy on the shelf at Branch-1",
                lib.branchService.countAvailableBookCopies(lib.branch1.id()).get(book.getId()) == 1L);

        // --- Scenario 2: A borrows the only copy. B can't borrow, so B (and then C) place a hold. Queue = B, C.
        Transaction txA = lib.borrow("A", book);
        check("S2: A borrowed it, due " + txA.dueAt().toLocalDate(), !txA.isReturned());
        expectError("S2: B tries to borrow", BookCopyNotAvailableException.class, () -> lib.borrow("B", book));
        Hold holdB = lib.holdService.placeHold("B", book.getId());
        Hold holdC = lib.holdService.placeHold("C", book.getId());
        check("S2: queue is B then C (first come, first served)", lib.queueOf(book).equals(List.of("B", "C")));
        expectError("S2: B cannot place a second hold", HoldException.class, () -> lib.holdService.placeHold("B", book.getId()));

        // --- Scenario 3: A returns 5 days late. 2 days are free, so 3 days x Rs10 x BASIC multiplier 1 = Rs30.
        lib.clock.setTo(txA.dueAt().plusDays(5));
        FineBreakdown fine = lib.returnService.returnBook(txA.id(), lib.branch1.id());
        System.out.println("      fine breakdown: " + fine);
        check("S3: fine is 30.00 (3 chargeable days)", same(fine.total(), "30"));
        check("S3: the fine is saved on the transaction",
                same(lib.transactionService.getById(txA.id()).fineBreakdown().total(), "30"));
        check("S3: B's hold is READY_FOR_PICKUP", holdB.getStatus() == HoldStatus.READY_FOR_PICKUP);
        check("S3: B was notified (email)", lib.email.count("B", EventType.HOLD_AVAILABLE) == 1);
        expectError("S3: a walk-in cannot take the copy reserved for B", BookCopyNotAvailableException.class, () -> lib.borrow("A", book));

        // --- Scenario 4: B never comes. After the 48h pickup window the scheduler expires the hold and the copy moves to C.
        lib.clock.advance(Duration.ofHours(49));
        check("S4: scheduler expired 1 hold", lib.scheduler.expireHolds() == 1);
        check("S4: B's hold is EXPIRED and B was told", holdB.getStatus() == HoldStatus.EXPIRED
                && lib.email.count("B", EventType.HOLD_EXPIRED) == 1);
        check("S4: C's hold is READY_FOR_PICKUP and C was notified", holdC.getStatus() == HoldStatus.READY_FOR_PICKUP
                && lib.email.count("C", EventType.HOLD_AVAILABLE) == 1);
        lib.borrow("C", book);
        check("S4: C collected the copy (hold FULFILLED)", holdC.getStatus() == HoldStatus.FULFILLED);

        check("The SMS channel was down all the time and nothing broke (" + lib.sms.attempts.get() + " failed attempts)",
                lib.sms.attempts.get() > 0);
    }

    /** Scenario 5 (+ the other borrow rules). */
    static void borrowRules() {
        title("Scenario 5: borrow rules (limit, suspension, unpaid fines)");
        Library lib = new Library();
        Member a = lib.newMember("A", basic());                       // BASIC: max 2 books
        Book b1 = lib.newBook("Book 1", "I-1", "Author One", Genre.NOVEL, "100", 1);
        Book b2 = lib.newBook("Book 2", "I-2", "Author Two", Genre.NOVEL, "100", 1);
        Book b3 = lib.newBook("Book 3", "I-3", "Author Three", Genre.NOVEL, "100", 1);

        lib.borrow("A", b1);
        lib.borrow("A", b2);
        expectError("S5: BASIC member borrows a 3rd book", BorrowException.class, () -> lib.borrow("A", b3));

        lib.memberService.suspendMember("A");
        expectError("Suspended member borrows", BorrowException.class, () -> lib.borrow("A", b3));
        lib.memberService.reactivate("A");

        // fines above the limit (Rs500) block borrowing until the member pays enough
        Library lib2 = new Library();
        Member f = lib2.newMember("F", basic());
        Book book = lib2.newBook("Book", "I-9", "Author", Genre.NOVEL, "100", 1);
        f.addUnpaidFine(new BigDecimal("600"));
        expectError("Member with Rs600 unpaid fines (limit 500) borrows", BorrowException.class, () -> lib2.borrow("F", book));
        lib2.memberService.recordFinePayment("F", new BigDecimal("200"));
        check("After paying Rs200 (Rs400 left) the member can borrow", lib2.borrow("F", book) != null);
    }

    /** Scenario 7: 50 threads fight for the last copy. */
    static void concurrency() throws InterruptedException {
        title("Scenario 7: 50 threads compete for 1 copy");
        Library lib = new Library();
        Book book = lib.newBook("Last Copy", "I-7", "Author", Genre.NOVEL, "100", 1);
        for (int i = 0; i < 50; i++) lib.newMember("M" + i, basic());

        AtomicInteger success = new AtomicInteger(), noCopy = new AtomicInteger(), unexpected = new AtomicInteger();
        race(50, i -> {
            try { lib.borrow("M" + i, book); success.incrementAndGet(); }
            catch (BookCopyNotAvailableException e) { noCopy.incrementAndGet(); }
            catch (RuntimeException e) { unexpected.incrementAndGet(); }
        });
        check("Exactly 1 of 50 succeeded (got " + success + ")", success.get() == 1);
        check("The other 49 were told 'not available' (got " + noCopy + ", unexpected errors: " + unexpected + ")",
                noCopy.get() == 49 && unexpected.get() == 0);
        check("Only one active loan exists", lib.transactionService.findActiveLoans().size() == 1);

        // Borrow limit under concurrency: one PREMIUM member (limit 5) tries to borrow 20 different books at once
        Library lib2 = new Library();
        lib2.newMember("P", premium());
        List<Book> books = new ArrayList<>();
        for (int i = 0; i < 20; i++) books.add(lib2.newBook("T" + i, "L-" + i, "Author", Genre.NOVEL, "100", 1));
        AtomicInteger borrowed = new AtomicInteger();
        race(20, i -> {
            try { lib2.borrow("P", books.get(i)); borrowed.incrementAndGet(); } catch (BorrowException ignored) { }
        });
        check("One member with limit 5 got exactly 5 of 20 books (got " + borrowed + ")", borrowed.get() == 5);

        // Hold queue under concurrency: 30 threads join the queue together, nobody is lost or duplicated
        Library lib3 = new Library();
        Book popular = lib3.newBook("Popular", "I-8", "Author", Genre.NOVEL, "100", 1);
        lib3.newMember("X", basic());
        lib3.borrow("X", popular);                                    // the only copy is out, so holds are allowed
        for (int i = 0; i < 30; i++) lib3.newMember("W" + i, basic());
        race(30, i -> lib3.holdService.placeHold("W" + i, popular.getId()));
        check("30 concurrent holds -> queue has 30 different members",
                lib3.queueOf(popular).size() == 30 && new HashSet<>(lib3.queueOf(popular)).size() == 30);
    }

    /** Scenario 8: search. */
    static void search() {
        title("Scenario 8: search");
        Library lib = new Library();
        Book foundation = lib.newBook("Foundation", "ISBN-F", "Isaac Asimov", Genre.SCI_FI, "600", 1);
        Book robot = lib.newBook("I, Robot", "ISBN-R", "Isaac Asimov", Genre.SCI_FI, "400", 0);
        lib.branchService.addBookCopies(lib.branch2.id(), robot.getId(), 1);        // I, Robot is only at Branch-2
        Book emma = lib.newBook("Emma", "ISBN-E", "Jane Austen", Genre.NOVEL, "300", 1);

        check("Author, partial + any letter case: 'ASIM' finds 2 books",
                lib.searchService.search(BookCriteria.writtenBy("ASIM")).size() == 2);
        check("ISBN, exact: 'ISBN-E' finds Emma", lib.searchService.search(BookCriteria.hasIsbn("ISBN-E")).equals(List.of(emma)));
        check("ISBN, exact: 'ISBN' alone finds nothing", lib.searchService.search(BookCriteria.hasIsbn("ISBN")).isEmpty());
        check("Genre NOVEL finds Emma", lib.searchService.search(BookCriteria.inGenre(Genre.NOVEL)).equals(List.of(emma)));
        check("Title, partial: 'robot' finds I, Robot", lib.searchService.search(BookCriteria.titleContains("robot")).equals(List.of(robot)));

        // Compound queries need no new search code: Predicate already has and / or / negate
        check("Compound: SCI_FI AND author 'asimov' AND NOT title 'robot' -> Foundation",
                lib.searchService.search(BookCriteria.inGenre(Genre.SCI_FI)
                        .and(BookCriteria.writtenBy("asimov"))
                        .and(BookCriteria.titleContains("robot").negate())).equals(List.of(foundation)));

        // Optional filters
        check("Filter by branch: Asimov books at Branch-2 -> I, Robot",
                lib.searchService.search(BookCriteria.writtenBy("asimov"), lib.branch2.id(), false).equals(List.of(robot)));
        lib.newMember("A", basic());
        lib.borrow("A", foundation);
        check("Filter 'available only': Foundation is out, so only I, Robot is left",
                lib.searchService.search(BookCriteria.writtenBy("asimov"), null, true).equals(List.of(robot)));
    }

    /** Scenario 9: 45 days late. Plus the cap. */
    static void fines() {
        title("Scenario 9: 45 days late -> long-term penalty on top of the daily fine; and the cap");
        Library lib = new Library();
        lib.newMember("A", basic());
        Book book = lib.newBook("Expensive", "I-10", "Author", Genre.NOVEL, "600", 1);
        Transaction tx = lib.borrow("A", book);
        lib.clock.setTo(tx.dueAt().plusDays(45));
        FineBreakdown fine = lib.returnService.returnBook(tx.id(), lib.branch1.id());
        System.out.println("      fine breakdown: " + fine);
        // 45 days - 2 free = 43 days x Rs10 = 430, + Rs50 long-term penalty = 480
        check("Fine is 480.00 = 430.00 base + 50.00 penalty", same(fine.total(), "480") && fine.lineItems().size() == 2);
        check("Breakdown is stored on the transaction", lib.transactionService.getById(tx.id()).fineBreakdown().equals(fine));
        check("Added to the member's unpaid fines", same(lib.memberService.getMemberById("A").getUnpaidFines(), "480"));

        // Same lateness, but the book only costs Rs200: the fine is capped at the book price
        Library cheap = new Library();
        cheap.newMember("A", basic());
        Book cheapBook = cheap.newBook("Cheap", "I-11", "Author", Genre.NOVEL, "200", 1);
        Transaction tx2 = cheap.borrow("A", cheapBook);
        cheap.clock.setTo(tx2.dueAt().plusDays(45));
        FineBreakdown capped = cheap.returnService.returnBook(tx2.id(), cheap.branch1.id());
        System.out.println("      fine breakdown: " + capped);
        check("Fine is capped at the book price (200.00)", same(capped.total(), "200"));
    }

    /** Cancelling a hold must pass the copy on correctly. Also: return at a different branch. */
    static void cancelAndOtherBranch() {
        title("Extra: cancel a hold, and return at another branch");
        Library lib = new Library();
        for (String id : List.of("A", "B", "C")) lib.newMember(id, basic());
        Book book = lib.newBook("Foundation", "I-12", "Author", Genre.SCI_FI, "100", 1);
        Transaction tx = lib.borrow("A", book);
        Hold holdB = lib.holdService.placeHold("B", book.getId());
        Hold holdC = lib.holdService.placeHold("C", book.getId());

        // A returns at Branch-2: the copy now belongs to Branch-2 and goes to B
        lib.returnService.returnBook(tx.id(), lib.branch2.id());
        BookCopy copy = lib.branchService.getBookCopy(tx.bookCopyId());
        check("Copy returned at Branch-2 now belongs to Branch-2 and is reserved", copy.getBranchId().equals(lib.branch2.id())
                && copy.getStatus() == BookCopyStatus.ON_HOLD);
        expectError("B tries to collect it at Branch-1", BorrowException.class, () -> lib.borrow("B", book));

        lib.holdService.cancelHold(holdB.getHoldId(), "B");
        check("B cancels -> copy passes to C", holdC.getStatus() == HoldStatus.READY_FOR_PICKUP && copy.getStatus() == BookCopyStatus.ON_HOLD);
        lib.holdService.cancelHold(holdC.getHoldId(), "C");
        check("C cancels, nobody is left -> copy is back on the shelf", copy.getStatus() == BookCopyStatus.AVAILABLE);
    }

    /** Librarian-style catalog rules + transaction queries. */
    static void catalogRules() {
        title("Extra: catalog / inventory rules and transaction queries");
        Library lib = new Library();
        lib.newMember("A", basic());
        Book book = lib.newBook("Foundation", "I-13", "Author", Genre.SCI_FI, "100", 1);
        Transaction tx = lib.borrow("A", book);

        expectError("Remove a borrowed copy", BorrowException.class, () -> lib.branchService.removeBookCopy(tx.bookCopyId()));
        expectError("Remove a book that is borrowed", BookInUseException.class, () -> lib.catalogService.removeBook(book.getId()));
        expectError("Add a second book with the same ISBN", IllegalArgumentException.class,
                () -> lib.newBook("Copycat", "I-13", "Author", Genre.NOVEL, "100", 0));
        check("Active loans of member A = 1", lib.transactionService.findActiveLoansOfMember("A").size() == 1);

        lib.clock.setTo(tx.dueAt().plusDays(1));
        check("All overdue loans = 1", lib.transactionService.findOverdueLoans().size() == 1);

        lib.returnService.returnBook(tx.id(), lib.branch1.id());
        expectError("Return the same loan twice", ReturnException.class, () -> lib.returnService.returnBook(tx.id(), lib.branch1.id()));
        check("History of the copy has 1 closed loan", lib.transactionService.findHistoryOfCopy(tx.bookCopyId()).size() == 1
                && lib.transactionService.findHistoryOfCopy(tx.bookCopyId()).get(0).isReturned());

        lib.catalogService.removeBook(book.getId());
        expectError("Book is gone from the catalog", BookNotFoundException.class, () -> lib.catalogService.getBook(book.getId()));
    }

    /** Scheduled operations: reminders and overdue notices are sent once, not again and again. */
    static void scheduledJobs() {
        title("Extra: scheduled reminders and overdue notices");
        Library lib = new Library();
        lib.newMember("A", basic());
        Book book = lib.newBook("Foundation", "I-14", "Author", Genre.SCI_FI, "100", 1);
        Transaction tx = lib.borrow("A", book);

        lib.clock.setTo(tx.dueAt().minusDays(5));
        check("5 days before due: no reminder yet", lib.scheduler.sendDueDateReminders() == 0);
        lib.clock.setTo(tx.dueAt().minusDays(1));
        check("1 day before due: 1 reminder sent", lib.scheduler.sendDueDateReminders() == 1);
        check("Running it again sends nothing", lib.scheduler.sendDueDateReminders() == 0);
        check("Member got exactly one reminder", lib.email.count("A", EventType.DUE_DATE_REMINDER) == 1);

        lib.clock.setTo(tx.dueAt().plusDays(3));
        check("3 days overdue: 1 overdue notice", lib.scheduler.sendOverdueNotices() == 1);
        check("Same day again: nothing", lib.scheduler.sendOverdueNotices() == 0);
        lib.clock.advance(Duration.ofDays(1));
        check("Next day: another notice", lib.scheduler.sendOverdueNotices() == 1);
    }

    public static void main(String[] args) throws Exception {
        holdQueueStory();          // scenarios 1, 2, 3, 4
        borrowRules();             // scenario 5
        // scenario 6 (role checks) is skipped: no role-based access in this version
        concurrency();             // scenario 7
        search();                  // scenario 8
        fines();                   // scenario 9
        cancelAndOtherBranch();
        catalogRules();
        scheduledJobs();

        System.out.printf("%n==== %d passed, %d failed ====%n", passed, failed);
        System.exit(failed == 0 ? 0 : 1);
    }
}
