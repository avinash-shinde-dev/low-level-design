package com.shikavani.lld.librarymanagement.repository;

import com.shikavani.lld.librarymanagement.models.Book;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

// This would be shared across branch
public class CatalogRepository implements InMemoryRepository<String, Book>{

    private final Map<String, Book> catalog = new ConcurrentHashMap<>();

    @Override
    public void save(Book book) {
        this.catalog.put(book.getId(), book);
    }

    @Override
    public Optional<Book> findById(String bookId) {
        return Optional.ofNullable(catalog.get(bookId));
    }

    @Override
    public List<Book> findAll() {
        return this.catalog.values().stream().toList();
    }

    @Override
    public Book delete(String id) {
        return this.catalog.remove(id);
    }


}
