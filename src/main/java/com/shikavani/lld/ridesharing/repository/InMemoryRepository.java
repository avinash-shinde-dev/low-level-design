package com.shikavani.lld.ridesharing.repository;

import java.util.List;

public interface InMemoryRepository<T> {
    void save(T t);
    T findById(String id);
    List<T> findAll();
}
