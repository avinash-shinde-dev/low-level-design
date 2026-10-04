package com.shikavani.lld.librarymanagement.repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * One generic in-memory store, shared by all repositories.
 * Each repository only tells it how to read an object's id.
 * Note: objects are stored by reference, so "save" after changing an object is for readability
 * (a real database would need it).
 */
public abstract class InMemoryRepository<T> {
    private final Map<String, T> store = new ConcurrentHashMap<>();
    private final Function<T, String> idOf;

    protected InMemoryRepository(Function<T, String> idOf) { this.idOf = idOf; }

    public void save(T item)               { store.put(idOf.apply(item), item); }
    public Optional<T> findById(String id) { return Optional.ofNullable(store.get(id)); }
    public List<T> findAll()               { return List.copyOf(store.values()); }
    public T delete(String id)             { return store.remove(id); }
}
