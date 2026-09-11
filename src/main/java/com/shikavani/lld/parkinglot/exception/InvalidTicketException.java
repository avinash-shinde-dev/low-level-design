package com.shikavani.lld.parkinglot.exception;

public class InvalidTicketException extends RuntimeException{
    public InvalidTicketException(String message) {
        super(message);
    }
}
