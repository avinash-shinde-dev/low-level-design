package com.shikavani.lld.ridesharing.model;

import com.shikavani.lld.ridesharing.enums.PaymentMethod;

public record UPIDetails(String upiId, String pin) implements PaymentDetails {

    @Override
    public PaymentMethod type() {
        return PaymentMethod.UPI;
    }
}
