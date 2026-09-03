package com.shikavani.lld.ridesharing.strategies.payment;

import com.shikavani.lld.ridesharing.enums.PaymentStatus;
import com.shikavani.lld.ridesharing.model.CardDetails;
import com.shikavani.lld.ridesharing.model.PaymentDetails;
import com.shikavani.lld.ridesharing.model.PaymentRequest;
import com.shikavani.lld.ridesharing.model.PaymentResponse;


public sealed class CardPayment implements PaymentStrategy permits CreditCardPayment, DebitCardPayment {
    @Override
    public final PaymentResponse pay(PaymentRequest paymentRequest) {
        System.out.println("****** Payment *****");
        System.out.println("Validate card details ");
        CardDetails cardDetails = validate(paymentRequest.paymentDetails());
        System.out.println("Ride details: " + paymentRequest.ride());
        System.out.println("Proceed with payment");
        return executePayment(paymentRequest, cardDetails);
    }

    private CardDetails validate(PaymentDetails paymentDetails){
        if(! (paymentDetails instanceof CardDetails cardDetails)){
            throw new IllegalArgumentException("Card payment required car");
        }

        if(cardDetails.cardNo() == null || cardDetails.cardNo().isBlank()){
            throw new IllegalArgumentException("Card Number is required");
        }

        if(cardDetails.cvv() == null || cardDetails.cvv().length() != 3){
            throw new IllegalArgumentException("Invalid cvv");
        }

        return cardDetails;
    }

    protected PaymentResponse executePayment(PaymentRequest paymentRequest, CardDetails cardDetails){
        throw new UnsupportedOperationException("Subclass should implement the payment execution");
    }
}
