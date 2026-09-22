package com.shikavani.lld.vendingmachine.service;

import com.shikavani.lld.vendingmachine.enums.PaymentType;
import com.shikavani.lld.vendingmachine.model.transaction.PaymentOutcome;
import com.shikavani.lld.vendingmachine.model.transaction.PaymentRequest;
import com.shikavani.lld.vendingmachine.registry.StrategyRegistry;
import com.shikavani.lld.vendingmachine.strategy.payment.PaymentStrategy;

public class PaymentService {
    private final StrategyRegistry<PaymentType, PaymentStrategy> paymentStrategies;

    public PaymentService(StrategyRegistry<PaymentType, PaymentStrategy> paymentStrategies) {
        this.paymentStrategies = paymentStrategies;
    }

    public PaymentOutcome pay(PaymentRequest paymentRequest){
        PaymentStrategy paymentStrategy = paymentStrategies.getOrThrow(paymentRequest.paymentTender().type());
        return paymentStrategy.pay(paymentRequest);
    }
}
