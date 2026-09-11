package com.shikavani.lld.parkinglot.strategy.pricing;

import com.shikavani.lld.parkinglot.model.Fee;
import com.shikavani.lld.parkinglot.model.Ticket;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Optional;

public class FlatPricingStrategy implements PricingStrategy{

    @Override
    public Optional<Fee> calculateFee(Ticket ticket) {
        return Optional.of(new Fee(BigDecimal.valueOf(30), Currency.getInstance("INR")));
    }
}
