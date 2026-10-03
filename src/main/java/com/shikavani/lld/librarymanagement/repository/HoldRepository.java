package com.shikavani.lld.librarymanagement.repository;

import com.shikavani.lld.librarymanagement.models.Hold;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class HoldRepository implements InMemoryRepository<String, Hold> {
    private final Map<String, Hold> holdMap = new ConcurrentHashMap<>();
    @Override
    public void save(Hold hold) {
        holdMap.put(hold.getHoldId(), hold);
    }

    @Override
    public Optional<Hold> findById(String holdId) {
        return Optional.ofNullable(holdMap.get(holdId));
    }

    @Override
    public List<Hold> findAll() {
        return holdMap.values().stream().toList();
    }

    @Override
    public Hold delete(String holdId) {
        return holdMap.remove(holdId);
    }
}
