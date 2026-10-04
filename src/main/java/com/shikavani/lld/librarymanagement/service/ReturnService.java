package com.shikavani.lld.librarymanagement.service;

import com.shikavani.lld.librarymanagement.enums.BookCopyStatus;
import com.shikavani.lld.librarymanagement.exception.ReturnException;
import com.shikavani.lld.librarymanagement.models.BookCopy;
import com.shikavani.lld.librarymanagement.models.Member;
import com.shikavani.lld.librarymanagement.models.Transaction;
import com.shikavani.lld.librarymanagement.models.FineBreakdown;
import com.shikavani.lld.librarymanagement.registry.LockRegistry;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.concurrent.locks.Lock;

public class ReturnService {
    private final BranchService branchService;
    private final HoldService holdService;
    private final FineCalculationService fineCalculationService;
    private final TransactionService transactionService;
    private final MemberService memberService;
    private final Clock clock;
    private final LockRegistry lock = LockRegistry.getInstance();

    public ReturnService(BranchService branchService, HoldService holdService,
                         FineCalculationService fineCalculationService, TransactionService transactionService,
                         MemberService memberService, Clock clock) {
        this.branchService = branchService;
        this.holdService = holdService;
        this.fineCalculationService = fineCalculationService;
        this.transactionService = transactionService;
        this.memberService = memberService;
        this.clock = clock;
    }

    /**
     * Return a book at ANY branch of the network.
     * Decision: a copy returned at another branch simply becomes part of THAT branch's inventory
     * (no IN_TRANSIT trip). If members are waiting for the title, the copy goes to the first one in
     * the queue, otherwise it goes back on the shelf. The fine is saved on the transaction and added to
     * the member's unpaid fines.
     *
     * @return the fine breakdown (total 0 when the book is on time)
     */
    public FineBreakdown returnBook(String transactionId, String branchId) {
        Objects.requireNonNull(transactionId, "Transaction id must not be null");
        branchService.getBranch(branchId); // Fail-Fast if the branch doesn't exist.

        // The ids of a transaction never change, so reading it before locking is safe (we only need them to pick the locks)
        Transaction snapshot = transactionService.getById(transactionId);

        Lock memberLock = lock.member(snapshot.memberId());
        memberLock.lock();
        try {
            Lock bookLock = lock.book(snapshot.bookId());
            bookLock.lock();
            try {
                Lock copyLock = lock.bookCopy(snapshot.bookCopyId());
                copyLock.lock();
                try {
                    Transaction stored = transactionService.getById(transactionId);   // read again, now under the locks
                    if (stored.isReturned()) {
                        throw new ReturnException("Transaction " + stored.id() + " has already been returned");
                    }
                    LocalDateTime returnedAt = LocalDateTime.now(clock);
                    BookCopy copy = branchService.getBookCopy(stored.bookCopyId());
                    Member member = memberService.getMemberById(stored.memberId());
                    FineBreakdown fine = fineCalculationService.calculate(stored, returnedAt);

                    // copy: BORROWED -> AVAILABLE, then straight to the head of the hold queue if somebody waits
                    copy.changeStatus(BookCopyStatus.AVAILABLE);
                    copy.moveToBranch(branchId);
                    holdService.fulfillNextHold(copy);
                    branchService.saveCopy(copy);

                    transactionService.saveTransaction(stored.closeTransaction(fine, returnedAt));
                    member.decrementBorrows();
                    if (fine.total().signum() > 0) member.addUnpaidFine(fine.total());
                    return fine;
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
}
