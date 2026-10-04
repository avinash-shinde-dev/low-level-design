package com.shikavani.lld.librarymanagement.repository;

import com.shikavani.lld.librarymanagement.models.Book;

public class CatalogRepository extends InMemoryRepository<Book> {
    public CatalogRepository() { super(Book::getId); }
}
