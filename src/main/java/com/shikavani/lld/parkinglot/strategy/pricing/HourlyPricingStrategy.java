package com.shikavani.lld.parkinglot.strategy.pricing;

import com.shikavani.lld.parkinglot.model.Fee;
import com.shikavani.lld.parkinglot.model.Ticket;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.Currency;
import java.util.Locale;
import java.util.Optional;

public class HourlyPricingStrategy implements PricingStrategy{
    private static final Fee HOURLY_CHARGES = new Fee(BigDecimal.valueOf(30), Currency.getInstance("INR"));
    @Override
    public Optional<Fee> calculateFee(Ticket ticket) {
        Duration duration = Duration.between(ticket.getEntryTime(), Instant.now());
        BigDecimal amount = HOURLY_CHARGES.value().multiply(BigDecimal.valueOf(duration.toHours()));
        return Optional.of(new Fee(amount, HOURLY_CHARGES.currency()));
    }
}
