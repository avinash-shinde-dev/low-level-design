package com.shikavani.lld.vendingmachine.model.payment;

import java.util.Currency;

public final class Coin extends Denomination {
    public Coin(Integer amount, Currency currency) {
        super(amount, currency);
    }

    @Override
    public String toString() {
        return "Coin{" +
                "amount=" + super.getAmount() +
                ", currency=" + super.getCurrency() +
                '}';
    }

}
