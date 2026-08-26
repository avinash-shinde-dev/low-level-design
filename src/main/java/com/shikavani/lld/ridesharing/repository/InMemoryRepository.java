package com.shikavani.lld.ridesharing.repository;

import java.util.List;

public interface InMemoryRepository<ID, T> {
    void save(T t);
    T findById(ID id);
    List<T> findAll();
}
