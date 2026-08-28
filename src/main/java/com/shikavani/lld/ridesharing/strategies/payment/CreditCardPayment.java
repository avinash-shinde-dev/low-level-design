package com.shikavani.lld.ridesharing.strategies.payment;

import com.shikavani.lld.ridesharing.enums.PaymentStatus;
import com.shikavani.lld.ridesharing.model.CardDetails;
import com.shikavani.lld.ridesharing.model.Fare;
import com.shikavani.lld.ridesharing.model.PaymentRequest;
import com.shikavani.lld.ridesharing.model.PaymentResponse;

import java.util.UUID;

public final class CreditCardPayment extends CardPayment{
    @Override
    public PaymentResponse pay(PaymentRequest paymentRequest){
        System.out.println("Proceeding with the credit card payment");
        System.out.println("Ride details: " + paymentRequest.ride());
        CardDetails cardDetails = (CardDetails) paymentRequest.paymentDetails();
        // validate details.
        System.out.println("Please enter the otp:");
        System.out.println("Payment of " + paymentRequest.fare() + " has been successfully completed");
        return new PaymentResponse(UUID.randomUUID().toString(), PaymentStatus.SUCCESS);
    }
}
