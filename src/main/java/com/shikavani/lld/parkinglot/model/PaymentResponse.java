package com.shikavani.lld.parkinglot.model;

import com.shikavani.lld.parkinglot.enums.PaymentStatus;

public record PaymentResponse(String transactionId, PaymentStatus paymentStatus ) {
}
