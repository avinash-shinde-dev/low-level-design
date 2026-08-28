package com.shikavani.lld.ridesharing.model;

import com.shikavani.lld.ridesharing.enums.PaymentMethod;

public record CardDetails(String cardNo, String expDate, String cvv, String pin) implements PaymentDetails {

    @Override
    public PaymentMethod type() {
        return PaymentMethod.CARD;
    }
}
