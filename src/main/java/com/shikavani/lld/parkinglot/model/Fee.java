package com.shikavani.lld.parkinglot.model;

import java.math.BigDecimal;
import java.util.Currency;

public record Fee(BigDecimal value, Currency currency) { }
