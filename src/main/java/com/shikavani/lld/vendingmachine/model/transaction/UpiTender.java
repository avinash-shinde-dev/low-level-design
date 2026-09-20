package com.shikavani.lld.vendingmachine.model.transaction;

import com.shikavani.lld.vendingmachine.enums.PaymentType;

public record UpiTender(String upiId, String upiPin) implements PaymentTender {
    @Override
    public PaymentType type() {
        return PaymentType.UPI;
    }
}
