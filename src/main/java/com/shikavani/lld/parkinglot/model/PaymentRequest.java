package com.shikavani.lld.parkinglot.model;

public record PaymentRequest(Ticket ticket, PaymentDetails paymentDetails) {
}
