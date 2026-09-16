package com.shikavani.lld.vendingmachine.inventory;

import com.shikavani.lld.vendingmachine.model.payment.Denomination;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public final class CashInventory {

    private final Map<Denomination, Integer> denominations;

    public CashInventory() {
        this.denominations = new ConcurrentHashMap<>();
    }

    public Map<Denomination, Integer> getDenominations() {
        return Map.copyOf(denominations);
    }

    public void addDenomination(Map<Denomination, Integer> denominations){
        denominations.forEach((d, q) -> this.denominations.merge(d, q, Integer::sum));
    }

    public void removeDenomination(Map<Denomination, Integer> denominations){
        denominations.forEach((d, q) ->
            this.denominations.compute( d, (k, v) -> {
                int available = v == null ? 0 : v;

                if(available < q) {
                    throw new IllegalStateException("Cannot remove " + q + " of " + d + ", only " + available + " available");
                }
                int remaining = available - q;
                return remaining == 0 ? null : remaining;
            }));
    }
}
