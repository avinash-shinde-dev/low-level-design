package com.shikavani.lld.ridesharing.strategies.payment;

import com.shikavani.lld.ridesharing.model.Fare;
import com.shikavani.lld.ridesharing.model.PaymentRequest;
import com.shikavani.lld.ridesharing.model.PaymentResponse;

public interface PaymentStrategy {
    PaymentResponse pay(PaymentRequest paymentRequest);
}
