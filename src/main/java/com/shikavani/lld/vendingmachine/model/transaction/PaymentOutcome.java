package com.shikavani.lld.vendingmachine.model.payment;

import java.util.Map;

public sealed interface PaymentOutcome permits PaymentOutcome.Insufficient, PaymentOutcome.Exact, PaymentOutcome.Overpaid {
    record Insufficient(Money money) implements PaymentOutcome {}
    record Exact() implements PaymentOutcome {}
    record Overpaid(Map<Denomination, Integer> change) implements PaymentOutcome {}

}
