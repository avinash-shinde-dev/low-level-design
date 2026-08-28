package com.shikavani.lld.ridesharing.model;

import com.shikavani.lld.ridesharing.enums.PaymentMethod;

public record CashDetails() implements PaymentDetails {

    @Override
    public PaymentMethod type() {
        return PaymentMethod.CASH;
    }
}
