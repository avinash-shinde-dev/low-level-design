package com.shikavani.lld.vendingmachine.model.transaction;

import com.shikavani.lld.vendingmachine.model.payment.Denomination;
import com.shikavani.lld.vendingmachine.model.payment.Money;

import java.util.Map;

public sealed interface PaymentOutcome permits PaymentOutcome.Insufficient, PaymentOutcome.Exact, PaymentOutcome.Overpaid {
    record Insufficient(Money money) implements PaymentOutcome {}
    record Exact() implements PaymentOutcome {}
    record Overpaid(Map<Denomination, Integer> change) implements PaymentOutcome {}

}
