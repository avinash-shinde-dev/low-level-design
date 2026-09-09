package com.shikavani.lld.parkinglot.repository;

import java.util.List;

public interface InMemoryRepository<K,T>{

    T save(T t);

    T findById(K k);

    List<T> findAll();
}
