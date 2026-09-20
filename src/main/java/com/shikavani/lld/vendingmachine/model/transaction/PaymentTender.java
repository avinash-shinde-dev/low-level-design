package com.shikavani.lld.vendingmachine.model.transaction;

import com.shikavani.lld.vendingmachine.enums.PaymentType;

public sealed interface PaymentTender permits CashTender, UpiTender {
    PaymentType type();
}
