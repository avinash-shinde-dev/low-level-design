package com.shikavani.lld.ridesharing.factory;

import com.shikavani.lld.ridesharing.enums.PaymentStrategyType;
import com.shikavani.lld.ridesharing.strategies.payment.*;

public final class PaymentStrategyFactory {

    public static PaymentStrategy getPaymentStrategy(PaymentStrategyType paymentStrategyType) {
        return switch (paymentStrategyType) {
            case CASH -> new CashPayment();
            case CREDIT_CARD ->  new CreditCardPayment();
            case DEBIT_CARD ->  new DebitCardPayment();
            case UPI ->  new UPIPayment();
        };
    }
}
