package com.shikavani.lld.parkinglot.model;

import com.shikavani.lld.parkinglot.enums.PaymentMode;

public record CardDetails(String cardNo, String cvv, String expDate) implements PaymentDetails {
    @Override
    public PaymentMode type() {
        return PaymentMode.CARD;
    }
}
