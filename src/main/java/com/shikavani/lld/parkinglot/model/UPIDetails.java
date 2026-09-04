package com.shikavani.lld.parkinglot.model;

import com.shikavani.lld.parkinglot.enums.PaymentMode;

public record UPIDetails(String upiId, String pin) implements PaymentDetails {
    @Override
    public PaymentMode type() {
        return PaymentMode.UPI;
    }
}
