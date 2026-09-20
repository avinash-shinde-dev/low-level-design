package com.shikavani.lld.vendingmachine.utils;

import com.shikavani.lld.vendingmachine.exception.CurrencyMismatchException;
import com.shikavani.lld.vendingmachine.model.payment.Denomination;
import com.shikavani.lld.vendingmachine.model.payment.Money;
import com.shikavani.lld.vendingmachine.model.payment.Price;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Map;

public final class MoneyCalculator {
    private final Money money;

    public MoneyCalculator(Money money) {
        this.money = money;
    }

    public Money fromDenominations(Map<Denomination, Integer> denominations) {
        BigDecimal currentAmount = this.money.amount();
        Currency resolvedCurrency = this.money.currency();

        for (Map.Entry<Denomination, Integer> entry : denominations.entrySet()) {
            Denomination denomination = entry.getKey();
            Currency denomCurrency = denomination.getCurrency();

            if (resolvedCurrency == null) {
                resolvedCurrency = denomCurrency;
            } else if (!resolvedCurrency.equals(denomCurrency)) {
                throw new IllegalStateException(
                        "Cannot compute total: mixed currencies found (" + resolvedCurrency + " and " + denomCurrency + ")");
            }

            currentAmount = currentAmount.add(BigDecimal.valueOf(denomination.getAmount()).multiply(BigDecimal.valueOf(entry.getValue())));
        }

        return new Money(currentAmount, resolvedCurrency != null ? resolvedCurrency : Currency.getInstance("INR")); // or however you handle empty
    }

    public Money change(Price price){
        // perform the check, currency mismatch
        if(!money.currency().equals(price.currency())){
            throw new CurrencyMismatchException(String.format("Currency mismatch; expected: %s, actual: %s", price.currency(), this.money.currency()));
        }
        BigDecimal change = this.money.amount().subtract(price.amount());

        return new Money(change, this.money.currency());
    }
}
