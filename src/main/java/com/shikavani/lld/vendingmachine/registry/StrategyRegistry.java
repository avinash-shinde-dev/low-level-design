package com.shikavani.lld.vendingmachine.registry;

import com.shikavani.lld.vendingmachine.enums.PaymentType;
import com.shikavani.lld.vendingmachine.strategy.payment.PaymentStrategy;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PaymentStrategyRegistry {
    private final Map<PaymentType, PaymentStrategy> REGISTRY = new ConcurrentHashMap<>();

    private PaymentStrategyRegistry(){}

    public void register(PaymentStrategy paymentStrategy){
        this.REGISTRY.put(paymentStrategy.type(), paymentStrategy);
    }

    public PaymentStrategy get(PaymentType paymentType){
        this.REGISTRY.get(paymentType);
    }
}
