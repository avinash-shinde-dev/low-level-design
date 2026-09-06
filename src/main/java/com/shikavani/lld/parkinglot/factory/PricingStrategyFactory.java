package com.shikavani.lld.parkinglot.factory;

import com.shikavani.lld.parkinglot.enums.PaymentMode;
import com.shikavani.lld.parkinglot.strategy.payment.PaymentStrategy;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public final class PaymentStrategyFactory {
    private static final Map<PaymentMode, PaymentStrategy> REGISTRY = new ConcurrentHashMap<>();

    private PaymentStrategyFactory() {}

    public static void register(PaymentMode type, PaymentStrategy paymentStrategy){
        REGISTRY.put(type,paymentStrategy);
    }

    public static Optional<PaymentStrategy> getPaymentStrategy(PaymentMode type){
        return Optional.of(REGISTRY.get(type));
    }
}
