package com.shikavani.lld.parkinglot.service;

import com.shikavani.lld.parkinglot.enums.PaymentMode;
import com.shikavani.lld.parkinglot.model.PaymentRequest;
import com.shikavani.lld.parkinglot.model.PaymentResponse;
import com.shikavani.lld.parkinglot.strategy.payment.PaymentStrategy;

public class PaymentService {

    private final PaymentStrategy paymentStrategy;

    public PaymentService(PaymentStrategy paymentStrategy) {
        this.paymentStrategy = paymentStrategy;
    }

    public PaymentResponse pay(PaymentRequest paymentRequest){
        System.out.println("Starting payment processing..");
        // perform business validation
        return this.paymentStrategy.pay(paymentRequest);
    }
}
