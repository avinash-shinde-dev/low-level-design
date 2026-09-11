package com.shikavani.lld.vendingmachine.model.payment;

import com.shikavani.lld.vendingmachine.enums.PaymentType;

public record UpiTender(String upiId, String upiPin) implements PaymentTender {
    @Override
    public PaymentType type() {
        return PaymentType.UPI;
    }
}
