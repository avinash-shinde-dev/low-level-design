package com.shikavani.lld.librarymanagement.service;

import com.shikavani.lld.librarymanagement.exception.TransactionNotFoundException;
import com.shikavani.lld.librarymanagement.models.Transaction;
import com.shikavani.lld.librarymanagement.repository.TransactionRepository;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/** Stores loans and answers the questions asked about them. */
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final Clock clock;

    public TransactionService(TransactionRepository transactionRepository, Clock clock) {
        this.transactionRepository = transactionRepository;
        this.clock = clock;
    }

    public void saveTransaction(Transaction transaction) {
        transactionRepository.save(Objects.requireNonNull(transaction, "Transaction must not be null"));
    }

    public Transaction getById(String transactionId) {
        return transactionRepository.findById(transactionId)
                .orElseThrow(() -> new TransactionNotFoundException("Transaction not found: " + transactionId));
    }

    /** Loans that are not returned yet (everybody). */
    public List<Transaction> findActiveLoans() {
        return transactionRepository.findAll().stream().filter(t -> !t.isReturned()).toList();
    }

    /** Active loans of one member. */
    public List<Transaction> findActiveLoansOfMember(String memberId) {
        return findActiveLoans().stream().filter(t -> t.memberId().equals(memberId)).toList();
    }

    /** Every loan (open and closed) of one physical copy. */
    public List<Transaction> findHistoryOfCopy(String bookCopyId) {
        return transactionRepository.findAll().stream().filter(t -> t.bookCopyId().equals(bookCopyId)).toList();
    }

    /** All loans that are past their due date and not returned. */
    public List<Transaction> findOverdueLoans() {
        LocalDateTime now = LocalDateTime.now(clock);
        return findActiveLoans().stream().filter(t -> t.isOverdueAt(now)).toList();
    }

    public boolean hasActiveLoanForBook(String bookId) {
        return findActiveLoans().stream().anyMatch(t -> t.bookId().equals(bookId));
    }
}
