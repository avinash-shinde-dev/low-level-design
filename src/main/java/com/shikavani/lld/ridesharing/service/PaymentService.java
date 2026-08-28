package com.shikavani.lld.ridesharing.service;

import com.shikavani.lld.ridesharing.enums.PaymentStrategyType;
import com.shikavani.lld.ridesharing.enums.RideStatus;
import com.shikavani.lld.ridesharing.exception.PaymentNotAllowedException;
import com.shikavani.lld.ridesharing.factory.PaymentStrategyFactory;
import com.shikavani.lld.ridesharing.model.Fare;
import com.shikavani.lld.ridesharing.model.PaymentRequest;
import com.shikavani.lld.ridesharing.model.PaymentResponse;
import com.shikavani.lld.ridesharing.model.Ride;
import com.shikavani.lld.ridesharing.strategies.payment.PaymentStrategy;

public class PaymentService {
    private final RideService rideService;

    public PaymentService(RideService rideService) {
        this.rideService = rideService;
    }

    public PaymentResponse payment(PaymentRequest paymentRequest) throws PaymentNotAllowedException{
        if(!paymentRequest.ride().getStatus().equals(RideStatus.RIDE_COMPLETED)){
            throw new PaymentNotAllowedException("Payment should be accepted only for completed rides whereas the current ride state is : " + paymentRequest.ride().getStatus().name());
        }
        PaymentStrategy paymentStrategy = PaymentStrategyFactory.getPaymentStrategy(paymentRequest.paymentStrategyType());
        return paymentStrategy.pay(paymentRequest);
    }
}
