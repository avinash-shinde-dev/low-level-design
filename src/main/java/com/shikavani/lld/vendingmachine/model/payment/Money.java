package com.shikavani.lld.vendingmachine.model;

import com.shikavani.lld.vendingmachine.exception.CurrencyMismatchException;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Map;

public record Money(BigDecimal amount, Currency currency) { }
