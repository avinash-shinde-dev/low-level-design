package com.shikavani.lld.vendingmachine.model.transaction;

import com.shikavani.lld.vendingmachine.enums.PaymentType;
import com.shikavani.lld.vendingmachine.model.payment.Denomination;

import java.util.Map;

public record CashTender(Map<Denomination, Integer> denominations) implements PaymentTender {
    @Override
    public PaymentType type() {
        return PaymentType.CASH;
    }
}
