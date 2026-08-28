package com.shikavani.lld.ridesharing.strategies.payment;

import com.shikavani.lld.ridesharing.enums.PaymentStatus;
import com.shikavani.lld.ridesharing.model.PaymentRequest;
import com.shikavani.lld.ridesharing.model.PaymentResponse;
import com.shikavani.lld.ridesharing.model.UPIDetails;

import java.util.UUID;

public class UPIPayment implements PaymentStrategy{
    @Override
    public PaymentResponse pay(PaymentRequest paymentRequest) {
        System.out.println("****** Payment *****");
        System.out.println("Ride details: " + paymentRequest.ride());
        // validate the upiId -
        UPIDetails upiDetails = (UPIDetails) paymentRequest.paymentDetails();
        if(!upiDetails.pin().equals("1234"))
            return new PaymentResponse(UUID.randomUUID().toString(), PaymentStatus.FAILED);

        System.out.println("UPI payment of amount: " + paymentRequest.fare() + "successfully completed.");
        return new PaymentResponse(UUID.randomUUID().toString(), PaymentStatus.SUCCESS);
    }
}
