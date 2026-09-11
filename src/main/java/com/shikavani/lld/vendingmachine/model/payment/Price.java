package com.shikavani.lld.vendingmachine.model;

import java.math.BigDecimal;
import java.util.Currency;

public record Price(BigDecimal amount, Currency currency) { }
