package com.shikavani.lld.librarymanagement.models;

import com.shikavani.lld.librarymanagement.enums.BookCopyStatus;
import com.shikavani.lld.librarymanagement.exception.BorrowException;

import java.util.Objects;
import java.util.UUID;

public final class BookCopy {
    private final String bookCopyId;
    private final Book book;
    private String branchId;
    private BookCopyStatus status;
    public BookCopy(Book book, String branchId) {
        this.bookCopyId = UUID.randomUUID().toString();
        this.book = Objects.requireNonNull(book, "Book must not be null");
        this.branchId = Objects.requireNonNull(branchId, "Branch Id must not be null");
        this.status = BookCopyStatus.AVAILABLE;
    }

    public String getBookCopyId() {
        return bookCopyId;
    }

    public BookCopyStatus getStatus() {
        return status;
    }

    public void markBorrowed(){
        if(!BookCopyStatus.AVAILABLE.equals(this.status)){
            throw new BorrowException("Cannot borrow the book as it is currently not in available state");
        }
        this.status = BookCopyStatus.BORROWED;
    }

    public void markRemoved(){
        if(BookCopyStatus.BORROWED.equals(this.status)){
            throw new BorrowException("Cannot removed book when it is already borrowed");
        }
        this.status = BookCopyStatus.REMOVED;
    }

    public Book getBook() {
        return book;
    }

    public String getBranchId() {
        return branchId;
    }

    public void setBranchId(String branchId) {
        this.branchId = branchId;
    }
}
