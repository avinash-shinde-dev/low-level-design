package com.shikavani.lld.librarymanagement.service;

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
    private final LockRegistry lock = LockRegistry.getInstance();

    public ReturnService(BranchService branchService, HoldService holdService, FineCalculationService fineCalculationService, TransactionService transactionService) {
        this.branchService = branchService;
        this.holdService = holdService;
        this.fineCalculationService = fineCalculationService;
        this.transactionService = transactionService;
    }

    public Fine returnBook(Transaction transaction, String branchId) {
        Objects.requireNonNull(transaction, "Transaction must not be null");
        Lock lockOnMember = lock.member(transaction.memberId());
        lockOnMember.lock();
        try {
            Lock lockOnTitle = lock.title(transaction.bookId());
            lockOnTitle.lock();
            try {
                Lock lockOnCopy = lock.bookCopy(transaction.bookCopyId());
                lockOnCopy.lock();
                try {
                    final String id = transaction.branchId() + ":" + transaction.bookCopyId();
                    BookCopy copy = this.branchService.getBookCopy(id);
                    // if the return branch is not same as issued branch
                    if (!transaction.branchId().equals(branchId)) {
                        copy.markInTransit();
                    } else {
                        copy.markAvailable();
                    }

                    // Here we should acquire lock on title
                    holdService.fulfillNextHold(copy).ifPresent(hold ->  {
                        // TODO: Notify Member
                    });

                    // save to db
                    this.branchService.saveCopy(copy);

                    // calculate fine
                    FineBreakdown breakdown = this.fineCalculationService.calculate(transaction);

                    // mark the transaction close
                    Transaction txn = transaction.closeTransaction(breakdown);
                    transactionService.saveTransaction(txn);

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
