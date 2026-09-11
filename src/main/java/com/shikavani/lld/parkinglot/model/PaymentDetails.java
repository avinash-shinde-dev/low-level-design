package com.shikavani.lld.parkinglot.model;

import com.shikavani.lld.parkinglot.enums.PaymentMode;

public sealed interface PaymentDetails permits CardDetails, UPIDetails, CashDetails {
    PaymentMode type();
}
