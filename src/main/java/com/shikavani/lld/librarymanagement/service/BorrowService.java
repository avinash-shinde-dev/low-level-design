package com.shikavani.lld.librarymanagement.service;

import com.shikavani.lld.librarymanagement.exception.BookCopyNotAvailableException;
import com.shikavani.lld.librarymanagement.exception.BookNotFoundException;
import com.shikavani.lld.librarymanagement.exception.BorrowException;
import com.shikavani.lld.librarymanagement.models.*;
import com.shikavani.lld.librarymanagement.registry.LockRegistry;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.locks.Lock;

public class BorrowService {

    private final BranchService branchService;
    private final MemberService memberService;
    private final CatalogService catalogService;
    private final TransactionService transactionService;
    private final HoldService holdService;
    private final LockRegistry lock = LockRegistry.getInstance();

    public BorrowService(BranchService branchService, MemberService memberService, CatalogService catalogService, TransactionService transactionService, HoldService holdService) {
        this.branchService = Objects.requireNonNull(branchService, "branch repository must not be null");
        this.memberService = Objects.requireNonNull(memberService, "Member service must not be null");
        this.catalogService = Objects.requireNonNull(catalogService, "Catalog service must not be null");
        this.transactionService = Objects.requireNonNull(transactionService, "Transaction service must not be null");
        this.holdService = Objects.requireNonNull(holdService, "Hold service must not be null");
    }

    public Transaction issueBook(String branchId, String memberId, String title) {
        Lock lockOnMember = lock.member(memberId);
        lockOnMember.lock();
        try {
            // find the member
            Member member = this.memberService.getMemberById(memberId);
            if (isNotAllowed(member)) {
                throw new BorrowException(String.format("Member %s cannot borrow book as member might be suspended or unpaid fines are exceeding membership threshold", member.getName()));
            }
            Book book = this.catalogService.searchFirst(BookCriteria.hasTitle(title)).orElseThrow(() -> new BookNotFoundException(String.format("Book: %s not found", title)));
            Lock lockOnTitle = lock.title(book.getId());
            lockOnTitle.lock();
            try {
                BookCopy copy = this.branchService.getAnyAvailableBookCopyFromBranch(branchId, book);
                Lock lockOnCopy = lock.bookCopy(copy.getBookCopyId());
                lockOnCopy.lock();
                try {
                    copy.markBorrowed();
                    this.branchService.saveCopy(copy);
                    // increment the borrows counter
                    member.incrementBorrows();
                    LocalDateTime issuedAt = LocalDateTime.now();
                    LocalDateTime dueAt = issuedAt.plusDays(member.getMembership().getLoanDurationDays());
                    Transaction transaction = new Transaction(UUID.randomUUID().toString(), copy.getBookCopyId(), book.getId(), branchId, memberId, issuedAt, dueAt, null, null);
                    this.transactionService.saveTransaction(transaction);
                    return transaction;
                } finally {
                    lockOnCopy.unlock();
                }
            } catch (BookCopyNotAvailableException bookCopyNotAvailableException){
                holdService.placeHold(memberId, book.getId());
                throw new BookCopyNotAvailableException("Book copy is not available, putting member in Waiting Queue");
            }finally {
                lockOnTitle.unlock();
            }
        } finally {
            lockOnMember.unlock();
        }

    }

    /**
     *
     * @param member
     * @return true if member is suspended or the unpaid fines are greater than threshold or
     * member has borrows than the max allowed borrows for membership.
     */
    private boolean isNotAllowed(Member member){
        return (member.isSuspended() ||
                member.getUnpaidFines().compareTo(member.getMembership().getUnpaidFineThreshold()) > 0 ||
                !member.hasFreeSlot());
    }
}
