package com.shikavani.lld.vendingmachine.strategy;

import com.shikavani.lld.vendingmachine.model.PaymentRequest;
import com.shikavani.lld.vendingmachine.model.PaymentResponse;

public interface PaymentStrategy {
    PaymentResponse pay(PaymentRequest request);
}
