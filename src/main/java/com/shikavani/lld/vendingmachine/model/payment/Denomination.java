package com.shikavani.lld.vendingmachine.model.payment;

import java.util.Currency;
import java.util.Objects;

public sealed class Denomination implements Comparable<Denomination> permits Coin, Note {
    private final Integer amount;
    private final Currency currency;

    public Denomination(Integer amount, Currency currency) {
        this.amount = amount;
        this.currency = currency;
    }

    public Integer getAmount() {
        return amount;
    }

    public Currency getCurrency() {
        return currency;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Denomination that = (Denomination) o;
        return Objects.equals(amount, that.amount) && Objects.equals(currency, that.currency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount, currency);
    }

    @Override
    public String toString() {
        return "Denomination{" +
                "amount=" + amount +
                ", currency=" + currency +
                '}';
    }

    @Override
    public int compareTo(Denomination o) {
        return this.getAmount().compareTo(o.getAmount());
    }
}

