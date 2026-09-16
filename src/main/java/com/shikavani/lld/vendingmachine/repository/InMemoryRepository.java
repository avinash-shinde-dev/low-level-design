package com.shikavani.lld.vendingmachine.repository;

import java.util.List;
import java.util.Optional;

public interface InMemoryRepository<ID, T> {
    T save(T t );

    Optional<T> findById(ID id);

    List<T> findAll();

    void delete(ID id);
}
