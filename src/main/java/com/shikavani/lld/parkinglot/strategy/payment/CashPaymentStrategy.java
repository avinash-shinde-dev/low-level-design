package com.shikavani.lld.parkinglot.strategy.payment;

import com.shikavani.lld.parkinglot.enums.PaymentStatus;
import com.shikavani.lld.parkinglot.model.CashDetails;
import com.shikavani.lld.parkinglot.model.PaymentDetails;
import com.shikavani.lld.parkinglot.model.PaymentResponse;
import com.shikavani.lld.parkinglot.model.PaymentRequest;

import java.util.UUID;

public class CashPaymentStrategy implements PaymentStrategy{
    @Override
    public PaymentResponse pay(PaymentRequest paymentRequest) {
        CashDetails paymentDetails = (CashDetails) paymentRequest.paymentDetails();
        System.out.println("Payment of : " + paymentDetails.fee() + " has been collected successfully");
        return new PaymentResponse(UUID.randomUUID().toString(), PaymentStatus.SUCCESS);
    }
}
