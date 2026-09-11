package com.shikavani.lld.vendingmachine.model;

import java.util.Currency;

public final class Note extends Denomination {

    public Note(Integer amount, Currency currency) {
        super(amount, currency);
    }

    @Override
    public String toString() {
        return "Note{" +
                "amount=" + super.getAmount() +
                ", currency=" + super.getCurrency() +
                '}';
    }
}
