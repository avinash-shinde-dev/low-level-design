package com.shikavani.lld.ridesharing.strategies.payment;

import com.shikavani.lld.ridesharing.enums.PaymentStatus;
import com.shikavani.lld.ridesharing.model.PaymentRequest;
import com.shikavani.lld.ridesharing.model.PaymentResponse;

public sealed class CardPayment implements PaymentStrategy permits CreditCardPayment, DebitCardPayment {
    @Override
    public PaymentResponse pay(PaymentRequest paymentRequest) {
        System.out.println("****** Payment *****");
        System.out.println("Ride details: " + paymentRequest.ride());
        System.out.println("Payment of amount: " + paymentRequest.fare() + " completed successfully using card.");
        return  new PaymentResponse("", PaymentStatus.SUCCESS);
    }
}
