package com.shikavani.lld.vendingmachine.model.payment;

import com.shikavani.lld.vendingmachine.strategy.payment.Transaction;

public record PaymentRequest(String id, Price price, Transaction transaction, PaymentTender paymentTender) { }
