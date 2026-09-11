package com.shikavani.lld.parkinglot.strategy.payment;

import com.shikavani.lld.parkinglot.enums.PaymentStatus;
import com.shikavani.lld.parkinglot.model.CardDetails;
import com.shikavani.lld.parkinglot.model.PaymentRequest;
import com.shikavani.lld.parkinglot.model.PaymentResponse;

import java.util.UUID;

public class DebitCardStrategy extends CardStrategy{

    @Override
    public PaymentResponse executePayment(CardDetails cardDetails, PaymentRequest paymentRequest){
        System.out.println("executing the payment using debit card");
        System.out.println("Connecting to gateway");
        System.out.println("Validating pin");
        return new PaymentResponse(UUID.randomUUID().toString(), PaymentStatus.SUCCESS);
    }
}
