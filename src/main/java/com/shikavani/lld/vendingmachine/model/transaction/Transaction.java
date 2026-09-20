package com.shikavani.lld.vendingmachine.model.transaction;

import com.shikavani.lld.vendingmachine.model.payment.Denomination;
import com.shikavani.lld.vendingmachine.model.payment.Money;
import com.shikavani.lld.vendingmachine.model.Product;
import com.shikavani.lld.vendingmachine.utils.MoneyCalculator;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// per-purchase, not per-payment-strategy-call
public final class Transaction {
    private final String transactionId;
    private final Product product;
    private final Map<Denomination, Integer> tendered = new ConcurrentHashMap<>();

    public Transaction(String transactionId, Product product) {
        this.transactionId = transactionId;
        this.product = product;
    }

    public Money addTender(Map<Denomination, Integer> denominations, Currency currency){
        denominations.forEach((d, q) -> tendered.merge(d, q, Integer::sum));
        return new MoneyCalculator(new Money(BigDecimal.ZERO, currency)).fromDenominations(denominations);
    }

    public Map<Denomination, Integer> getTender(){
        return Map.copyOf(this.tendered);
    }

    public String getTransactionId() {
        return transactionId;
    }

    public Product getProduct() {
        return product;
    }

}
