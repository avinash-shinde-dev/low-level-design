package com.shikavani.lld.ridesharing.model;

import com.shikavani.lld.ridesharing.enums.PaymentMethod;

public sealed interface PaymentDetails permits CardDetails, UPIDetails, CashDetails{
    PaymentMethod type();
}


