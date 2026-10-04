package com.shikavani.lld.librarymanagement.repository;

import com.shikavani.lld.librarymanagement.models.Transaction;

public class TransactionRepository extends InMemoryRepository<Transaction> {
    public TransactionRepository() { super(Transaction::id); }
}
