package com.shikavani.lld.parkinglot.model;

import com.shikavani.lld.parkinglot.enums.PaymentMode;

public record CashDetails(Fee fee) implements PaymentDetails{
    @Override
    public PaymentMode type() {
        return PaymentMode.CASH;
    }

}
