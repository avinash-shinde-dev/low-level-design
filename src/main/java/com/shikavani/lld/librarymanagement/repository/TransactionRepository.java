package com.shikavani.lld.librarymanagement.repository;


import com.shikavani.lld.librarymanagement.models.Transaction;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class TransactionRepository implements InMemoryRepository<String, Transaction> {

    private final Map<String, Transaction> transactionMap = new ConcurrentHashMap<>();
    @Override
    public void save(Transaction transaction) {
        transactionMap.put(transaction.id(), transaction);
    }

    @Override
    public Optional<Transaction> findById(String transactionId) {
        return Optional.ofNullable(transactionMap.get(transactionId));
    }

    @Override
    public List<Transaction> findAll() {
        return transactionMap.values().stream().toList();
    }

    @Override
    public Transaction delete(String transactionId) {
        return transactionMap.remove(transactionId);
    }
}
