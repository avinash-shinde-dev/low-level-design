package com.shikavani.lld.vendingmachine.strategy.payment;

import com.shikavani.lld.vendingmachine.enums.PaymentType;
import com.shikavani.lld.vendingmachine.model.transaction.PaymentRequest;
import com.shikavani.lld.vendingmachine.model.transaction.PaymentOutcome;

public interface PaymentStrategy {
    PaymentType type();
    PaymentOutcome pay(PaymentRequest request);
}
