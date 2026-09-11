package com.shikavani.lld.parkinglot.strategy.payment;

import com.shikavani.lld.parkinglot.model.PaymentResponse;
import com.shikavani.lld.parkinglot.model.PaymentRequest;

public interface PaymentStrategy {
    PaymentResponse pay(PaymentRequest paymentRequest);
}
