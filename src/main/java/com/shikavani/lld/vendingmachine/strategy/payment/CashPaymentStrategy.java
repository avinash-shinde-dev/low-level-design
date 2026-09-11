package com.shikavani.lld.vendingmachine.strategy;

import com.shikavani.lld.vendingmachine.exception.CurrencyMismatchException;
import com.shikavani.lld.vendingmachine.model.*;

public class CashPaymentStrategy implements PaymentStrategy{
    @Override
    public PaymentResponse pay(PaymentRequest request) {
        CashDetails cashDetails = (CashDetails) request.paymentDetails();
        Money totalMoney = getTotalMoney(cashDetails.denominations());
        Price price = request.price();
        // perform the check, currency mismatch
        if(!totalMoney.currency().equals(price.currency())){
            throw new CurrencyMismatchException(String.format("Currency mismatch; expected: %s, actual: %s", price.currency(), totalMoney.currency()));
        }

        int result = totalMoney.amount().compareTo(price.amount());
        return new PaymentResponse();
    }
}
