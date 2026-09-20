package com.shikavani.lld.vendingmachine.strategy.payment;

import com.shikavani.lld.vendingmachine.enums.PaymentType;
import com.shikavani.lld.vendingmachine.model.transaction.PaymentRequest;
import com.shikavani.lld.vendingmachine.model.transaction.PaymentOutcome;

public class UpiPaymentStrategy implements PaymentStrategy{
    @Override
    public PaymentType type() {
        return PaymentType.UPI;
    }

    @Override
    public PaymentOutcome pay(PaymentRequest request) {
        return new PaymentOutcome.Exact();
    }
}
