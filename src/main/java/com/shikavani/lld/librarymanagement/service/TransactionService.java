package com.shikavani.lld.librarymanagement.service;

import com.shikavani.lld.librarymanagement.models.Transaction;
import com.shikavani.lld.librarymanagement.repository.TransactionRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class TransactionService {
    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public void saveTransaction(Transaction transaction){
        Objects.requireNonNull(transaction, "Transaction must not be null");
        this.transactionRepository.save(transaction);
    }

    public List<Transaction> findActiveTransaction(String memberId){
        return this.transactionRepository.findAll()
                .stream()
                .filter(transaction -> memberId.equals(transaction.memberId()))
                .toList();
    }

    public List<Transaction> findAllOverDueTransactions(){
        return this.transactionRepository.findAll()
                .stream()
                .filter(transaction -> transaction.returnTimeStamp() == null && transaction.dueAt().isBefore(LocalDateTime.now()))
                .toList();
    }
}
