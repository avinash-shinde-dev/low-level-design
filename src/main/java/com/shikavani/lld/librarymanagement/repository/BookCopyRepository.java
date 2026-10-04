package com.shikavani.lld.librarymanagement.repository;

import com.shikavani.lld.librarymanagement.models.BookCopy;

public class BookCopyRepository extends InMemoryRepository<BookCopy> {
    public BookCopyRepository() { super(BookCopy::getBookCopyId); }
}
