package com.shikavani.lld.parkinglot.strategy.pricing;

import com.shikavani.lld.parkinglot.model.Fee;
import com.shikavani.lld.parkinglot.model.Ticket;

import java.util.Optional;

public interface PricingStrategy {

    Optional<Fee> calculateFee(Ticket ticket);
}
