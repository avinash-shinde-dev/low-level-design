package com.shikavani.lld.vendingmachine.strategy.payment;

import com.shikavani.lld.vendingmachine.enums.PaymentType;
import com.shikavani.lld.vendingmachine.exception.CurrencyMismatchException;
import com.shikavani.lld.vendingmachine.inventory.CashInventory;
import com.shikavani.lld.vendingmachine.model.payment.*;
import com.shikavani.lld.vendingmachine.model.transaction.CashTender;
import com.shikavani.lld.vendingmachine.model.transaction.PaymentOutcome;
import com.shikavani.lld.vendingmachine.model.transaction.PaymentRequest;
import com.shikavani.lld.vendingmachine.utils.ChangeCalculator;

import java.util.HashMap;
import java.util.Map;

public class CashPaymentStrategy implements PaymentStrategy{

    private final ChangeCalculator changeCalculator;
    private final CashInventory cashInventory;
    public CashPaymentStrategy(ChangeCalculator changeCalculator, CashInventory cashInventory) {
        this.changeCalculator = changeCalculator;
        this.cashInventory = cashInventory;
    }

    @Override
    public PaymentType type() {
        return PaymentType.CASH;
    }

    @Override
    public PaymentOutcome pay(PaymentRequest paymentRequest) {

        Price price = paymentRequest.price();
        CashTender cashTender = (CashTender) paymentRequest.paymentTender();
        Money amountTendered = paymentRequest.transaction().addTender(cashTender.denominations(), price.currency());

        if (!amountTendered.currency().equals(price.currency())) {
            throw new CurrencyMismatchException(
                    String.format("Currency mismatch; expected: %s, actual: %s", price.currency(), amountTendered.currency()));
        }
        int result = amountTendered.amount().compareTo(price.amount());
        if (result < 0) {
            return new PaymentOutcome.Insufficient(new Money(price.amount().subtract(amountTendered.amount()), price.currency()));
        }
        if (result == 0) {
            return new PaymentOutcome.Exact();
        }
        // calculate current + added tender
        Map<Denomination, Integer> current = new HashMap<>(this.cashInventory.getDenominations());
        Map<Denomination, Integer> change = changeCalculator.makeChange(amountTendered.amount().subtract(price.amount()), getCurrentBalance(current, paymentRequest.transaction().getTender()));

        return new PaymentOutcome.Overpaid(change);
    }

    public Map<Denomination, Integer> getCurrentBalance(Map<Denomination, Integer> current, Map<Denomination, Integer> tendered){
        tendered.forEach(((denomination, integer) -> current.merge(denomination, integer, Integer::sum)));
        return current;
    }
}
