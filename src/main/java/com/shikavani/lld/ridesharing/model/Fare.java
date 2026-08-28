package com.shikavani.lld.ridesharing.model;

import java.math.BigDecimal;
import java.util.Currency;

public class Fare {
    private BigDecimal amount;
    private Currency currency;

    public Fare() {}
    public Fare(BigDecimal amount, Currency currency) {
        this.amount = amount;
        this.currency = currency;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    @Override
    public String toString() {
        return amount.toString() + currency.getSymbol();
    }
}

