package com.shikavani.lld.ridesharing.model;

import com.shikavani.lld.ridesharing.enums.PaymentStatus;

public record PaymentResponse(String transactionId, PaymentStatus paymentStatus) {
}
