package com.shikavani.lld.librarymanagement.exception;

public class BookInUseException extends RuntimeException {
    public BookInUseException(String message) { super(message); }
}
