package com.shikavani.lld.ridesharing.model;

import java.math.BigDecimal;
import java.util.Currency;

public record Rate(BigDecimal baseFare, BigDecimal perKmRate) { }
