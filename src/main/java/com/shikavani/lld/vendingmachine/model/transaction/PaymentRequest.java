package com.shikavani.lld.vendingmachine.model.transaction;

import com.shikavani.lld.vendingmachine.model.payment.Price;

public record PaymentRequest(String id, Price price, Transaction transaction, PaymentTender paymentTender) { }
