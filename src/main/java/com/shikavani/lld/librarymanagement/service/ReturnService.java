package com.shikavani.lld.librarymanagement.service;

import com.shikavani.lld.librarymanagement.exception.ReturnException;
import com.shikavani.lld.librarymanagement.models.*;
import com.shikavani.lld.librarymanagement.models.fine.Fine;
import com.shikavani.lld.librarymanagement.models.fine.FineBreakdown;
import com.shikavani.lld.librarymanagement.registry.LockRegistry;

import java.util.Currency;
import java.util.Objects;
import java.util.concurrent.locks.Lock;

public class ReturnService {
    private final BranchService branchService;
    private final HoldService holdService;
    private final FineCalculationService fineCalculationService;
    private final TransactionService transactionService;
    private final MemberService memberService;
    private final LockRegistry lock = LockRegistry.getInstance();

    public ReturnService(BranchService branchService, HoldService holdService, FineCalculationService fineCalculationService, TransactionService transactionService, MemberService memberService) {
        this.branchService = branchService;
        this.holdService = holdService;
        this.fineCalculationService = fineCalculationService;
        this.transactionService = transactionService;
        this.memberService = memberService;
    }

    public Fine returnBook(Transaction transaction, String branchId) {
        Objects.requireNonNull(transaction, "Transaction must not be null");
        Objects.requireNonNull(branchId, "Branch Id must not be null");
        // we can fail fast by check if branch exists or not?
        Lock lockOnMember = lock.member(transaction.memberId());
        lockOnMember.lock();
        try {
            Lock lockOnTitle = lock.title(transaction.bookId());
            lockOnTitle.lock();
            try {
                Lock lockOnCopy = lock.bookCopy(transaction.bookCopyId());
                lockOnCopy.lock();
                try {
                    Transaction stored = this.transactionService.getById(transaction.id());
                    // If book is already returned
                    if (stored.returnTimeStamp() != null) {
                        throw new ReturnException("Transaction " + stored.id() + " has already been returned");
                    }
                    BookCopy copy = this.branchService.getBookCopy(transaction.bookCopyId());
                    Member member = this.memberService.getMemberById(transaction.memberId());
                    // 1. calculate fine
                    FineBreakdown breakdown = this.fineCalculationService.calculate(transaction);

                    // 2. if the return branch is not same as issued branch
                    if (!transaction.branchId().equals(branchId)) {
                        copy.markInTransit();
                    } else {
                        copy.markAvailable();
                        // Here we should acquire lock on title
                        holdService.fulfillNextHold(copy);
                    }

                    // save to db
                    this.branchService.saveCopy(copy);
                    // mark the transaction close
                    Transaction txn = transaction.closeTransaction(breakdown);
                    transactionService.saveTransaction(txn);

                    // decrement the borrows
                    member.decrementBorrows();

                    return new Fine(breakdown.total(), Currency.getInstance("INR"));

                } finally {
                    lockOnCopy.unlock();
                }
            } finally {
                lockOnTitle.unlock();
            }
        } finally {
            lockOnMember.unlock();
        }
    }
}
