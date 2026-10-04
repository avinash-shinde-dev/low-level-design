package com.shikavani.lld.librarymanagement.service;

import com.shikavani.lld.librarymanagement.enums.BookCopyStatus;
import com.shikavani.lld.librarymanagement.exception.BorrowException;
import com.shikavani.lld.librarymanagement.models.*;
import com.shikavani.lld.librarymanagement.registry.LockRegistry;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.locks.Lock;

public class BorrowService {
    private final BranchService branchService;
    private final MemberService memberService;
    private final CatalogService catalogService;
    private final TransactionService transactionService;
    private final HoldService holdService;
    private final Clock clock;
    private final LockRegistry lock = LockRegistry.getInstance();

    public BorrowService(BranchService branchService, MemberService memberService, CatalogService catalogService,
                         TransactionService transactionService, HoldService holdService, Clock clock) {
        this.branchService = Objects.requireNonNull(branchService);
        this.memberService = Objects.requireNonNull(memberService);
        this.catalogService = Objects.requireNonNull(catalogService);
        this.transactionService = Objects.requireNonNull(transactionService);
        this.holdService = Objects.requireNonNull(holdService);
        this.clock = Objects.requireNonNull(clock);
    }

    /**
     * Borrow a book at a branch.
     * - Fails with BorrowException if the member is suspended, owes too much, or is at the borrow limit.
     * - Fails with BookCopyNotAvailableException if no copy is free. Nothing else happens then:
     *   the member can place a hold with HoldService.placeHold().
     * - If the member has a reserved copy waiting (hold READY_FOR_PICKUP), that copy is the one they get.
     *
     * Locks are taken in the fixed order member -> book -> copy (see LockRegistry), so two threads can
     * never grab the same copy, never push a member over the limit, and can never deadlock.
     */
    public Transaction borrowBook(String branchId, String memberId, String bookId) {
        branchService.getBranch(branchId);
        Book book = catalogService.getBook(bookId);

        Lock memberLock = lock.member(memberId);
        memberLock.lock();
        try {
            Member member = memberService.getMemberById(memberId);
            requireAllowedToBorrow(member);            // checked under the member lock, so the limit check is reliable

            Lock bookLock = lock.book(bookId);
            bookLock.lock();
            try {
                Optional<Hold> reservation = holdService.findReadyHold(memberId, bookId);
                BookCopy copy = reservation.isPresent()
                        ? reservedCopyAt(branchId, reservation.get())
                        : branchService.findAvailableCopy(branchId, bookId);

                Lock copyLock = lock.bookCopy(copy.getBookCopyId());
                copyLock.lock();
                try {
                    // AVAILABLE -> BORROWED, or ON_HOLD -> BORROWED for the member's own reserved copy.
                    // Any other status is rejected by the status table in BookCopy.
                    copy.changeStatus(BookCopyStatus.BORROWED);
                    branchService.saveCopy(copy);
                    reservation.ifPresent(h -> holdService.markFulfilled(h.getHoldId()));

                    member.incrementBorrows();
                    LocalDateTime issuedAt = LocalDateTime.now(clock);
                    LocalDateTime dueAt = issuedAt.plusDays(member.getMembership().loanDurationDays());
                    Transaction transaction = new Transaction(UUID.randomUUID().toString(), copy.getBookCopyId(),
                            bookId, branchId, memberId, issuedAt, dueAt, null, null);
                    transactionService.saveTransaction(transaction);
                    return transaction;
                } finally {
                    copyLock.unlock();
                }
            } finally {
                bookLock.unlock();
            }
        } finally {
            memberLock.unlock();
        }
    }

    /** A reserved copy must be collected at the branch where it is waiting. */
    private BookCopy reservedCopyAt(String branchId, Hold hold) {
        BookCopy copy = branchService.getBookCopy(hold.getBookCopyId());
        if (!copy.getBranchId().equals(branchId)) {
            throw new BorrowException("Your reserved copy is waiting at branch "
                    + branchService.getBranch(copy.getBranchId()).name());
        }
        return copy;
    }

    private void requireAllowedToBorrow(Member member) {
        Membership rules = member.getMembership();
        if (member.isSuspended()) {
            throw new BorrowException("Member " + member.getName() + " is suspended and cannot borrow");
        }
        if (member.getUnpaidFines().compareTo(rules.unpaidFineLimit()) > 0) {
            throw new BorrowException("Member " + member.getName() + " has unpaid fines of " + member.getUnpaidFines()
                    + " (limit " + rules.unpaidFineLimit() + "); please pay before borrowing");
        }
        if (!member.hasFreeSlot()) {
            throw new BorrowException("Member " + member.getName() + " has reached the borrow limit of "
                    + rules.maximumBorrows());
        }
    }
}
