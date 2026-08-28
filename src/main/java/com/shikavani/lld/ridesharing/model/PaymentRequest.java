package com.shikavani.lld.ridesharing.model;

import com.shikavani.lld.ridesharing.enums.PaymentStrategyType;

public record PaymentRequest(Ride ride, Fare fare, PaymentStrategyType paymentStrategyType, PaymentDetails paymentDetails) {
}
