package com.shikavani.lld.ridesharing.strategies.payment;

import com.shikavani.lld.ridesharing.enums.PaymentStatus;
import com.shikavani.lld.ridesharing.model.*;

import java.util.UUID;

public final class DebitCardPayment extends CardPayment{

    @Override
    public PaymentResponse executePayment(PaymentRequest paymentRequest, CardDetails cardDetails){
        System.out.println("Proceed with debit card payment");
        System.out.println("Please enter the otp:");
        System.out.println("Verifying your otp");
        System.out.println("Payment of " + paymentRequest.fare() + " has been successfully completed");
        return new PaymentResponse(UUID.randomUUID().toString(), PaymentStatus.SUCCESS);
    }
}
