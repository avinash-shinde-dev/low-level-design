package com.shikavani.lld.ridesharing.exception;

public class PaymentNotAllowedException extends RuntimeException{

    public PaymentNotAllowedException(String msg){
        super(msg);
    }
}
