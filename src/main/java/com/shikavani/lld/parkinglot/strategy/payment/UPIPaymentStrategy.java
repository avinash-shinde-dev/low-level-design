package com.shikavani.lld.parkinglot.strategy.payment;


import com.shikavani.lld.parkinglot.enums.PaymentStatus;
import com.shikavani.lld.parkinglot.model.PaymentDetails;
import com.shikavani.lld.parkinglot.model.PaymentResponse;
import com.shikavani.lld.parkinglot.model.PaymentRequest;
import com.shikavani.lld.parkinglot.model.UPIDetails;

import java.util.UUID;

public class UPIPaymentStrategy implements PaymentStrategy{
    @Override
    public PaymentResponse pay(PaymentRequest paymentRequest) {
        UPIDetails upiDetails = validate(paymentRequest.paymentDetails());

        System.out.println("Enter your pin");
        if(!"1234".equals(upiDetails.pin()))
            return new PaymentResponse(UUID.randomUUID().toString(), PaymentStatus.FAILED);

        return new PaymentResponse(UUID.randomUUID().toString(), PaymentStatus.SUCCESS);
    }

    private UPIDetails validate(PaymentDetails paymentDetails){
        if(!(paymentDetails instanceof UPIDetails upiDetails)){
            throw new IllegalArgumentException("UPI Payment need upi details");
        }

        if(!isValid(upiDetails.upiId())){
            throw new IllegalArgumentException("Invalid UPI id");
        }
        return upiDetails;
    }

    private boolean isValid(String str){
        if(str == null || str.isBlank()){
            return false;
        }
        return str.contains("@Oksbi");
    }
}
