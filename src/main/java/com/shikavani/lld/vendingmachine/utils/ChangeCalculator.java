package com.shikavani.lld.vendingmachine.utils;

import com.shikavani.lld.vendingmachine.exception.ExactChangeUnavailableException;
import com.shikavani.lld.vendingmachine.inventory.CashInventory;
import com.shikavani.lld.vendingmachine.model.payment.Denomination;
import com.shikavani.lld.vendingmachine.model.transaction.Transaction;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public final class ChangeCalculator {

    public Map<Denomination, Integer> makeChange(BigDecimal change, Map<Denomination, Integer> current){

        // can we make the amount with the possible coins
        List<Denomination> denominationsList = new ArrayList<>();
        current.forEach((denomination, quantity) -> {
            for (int i = 0; i < quantity; i++) {
                denominationsList.add(denomination);
            }
        });

        // we need the fewest denominations, so sort it in reverse order
        Collections.sort(denominationsList, Comparator.reverseOrder());

        System.out.println("Denominations: " + denominationsList);
        // if we got the value in the change, then we won't throw exception
        Map<Denomination, Integer> refund = getChange(denominationsList, change );
        return refund;
    }

    private Map<Denomination, Integer> getChange(List<Denomination> denominations, BigDecimal amount){
        Map<Denomination, Integer> change = new ConcurrentHashMap<>();
        return getChangeInternal(denominations, amount, change, 0);
    }

    // check if it is possible to calculate the change if not return ExactChangeUnavailableException
    private Map<Denomination, Integer> getChangeInternal(List<Denomination> denominations, BigDecimal amount, Map<Denomination, Integer> change, Integer index){

        if(BigDecimal.ZERO.equals(amount)){
            return change;
        }

        if(index >= denominations.size()){
            // at the end of the list
            throw new ExactChangeUnavailableException("Unable to return the exact change");
        }

        // take it, reduce the amount
        if(BigDecimal.valueOf(denominations.get(index).getAmount()).compareTo(amount) <= 0){
            // take it
            change.merge(denominations.get(index), 1, Integer::sum);

            Map<Denomination, Integer> result =  getChangeInternal(denominations, amount.subtract(BigDecimal.valueOf(denominations.get(index).getAmount())), change, index+1);

            if(!result.isEmpty()){
                return result;
            }
            // not taking we need to reduce the count
            change.computeIfPresent(denominations.get(index),
                    (key, value) -> value == 1 ? null : value - 1 );
        }

        return getChangeInternal(denominations, amount, change, index+1);
    }
}
