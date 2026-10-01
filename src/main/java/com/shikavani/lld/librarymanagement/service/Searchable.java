package com.shikavani.lld.librarymanagement.service;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public interface Searchable<T> {
    List<T> search(Predicate<T> criteria);
    Optional<T> searchFirst(Predicate<T> criteria);

}
