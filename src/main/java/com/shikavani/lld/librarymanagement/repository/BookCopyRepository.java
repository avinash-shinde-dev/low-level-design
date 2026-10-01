package com.shikavani.lld.librarymanagement.repository;

import com.shikavani.lld.librarymanagement.models.BookCopy;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class BookCopyRepository implements InMemoryRepository<String, BookCopy> {
    private final Map<String, BookCopy> bookCopyMap = new ConcurrentHashMap<>();

    @Override
    public void save(BookCopy bookCopy) {
        this.bookCopyMap.put( bookCopy.getBranchId() + ":" + bookCopy.getBookCopyId(), bookCopy);
    }

    @Override
    public Optional<BookCopy> findById(String id) {
        return Optional.ofNullable(bookCopyMap.get(id));
    }

    @Override
    public List<BookCopy> findAll() {
        return this.bookCopyMap.values().stream().toList();
    }

    @Override
    public BookCopy delete(String id) {
        return this.bookCopyMap.remove(id);
    }
}
