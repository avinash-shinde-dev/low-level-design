package com.shikavani.lld.vendingmachine.model.payment;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Objects;

public record Price(BigDecimal amount, Currency currency) {
    public Price {
        Objects.requireNonNull(amount);
        Objects.requireNonNull(currency);

        if (amount.signum() < 0) {
            throw new IllegalArgumentException(
                    "Price cannot be negative");
        }
    }
}
