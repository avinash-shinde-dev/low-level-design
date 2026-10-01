package com.shikavani.lld.librarymanagement.repository;

import java.util.List;
import java.util.Optional;

public interface InMemoryRepository<ID, T> {

    void save(T t);

    Optional<T> findById(ID id);

    List<T> findAll();

    T delete(ID id);
}
