package com.shikavani.lld.ridesharing.strategies.farecalculation;

import com.shikavani.lld.ridesharing.enums.FareCalculationStrategyType;
import com.shikavani.lld.ridesharing.model.FareRateProvider;
import com.shikavani.lld.ridesharing.model.Fare;
import com.shikavani.lld.ridesharing.model.Rate;
import com.shikavani.lld.ridesharing.model.TripDetails;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.Currency;

public class LuxuryFareCalculationStrategy implements FareCalculationStrategy{

    private final FareRateProvider fareRateProvider;
    public LuxuryFareCalculationStrategy(FareRateProvider fareRateProvider) {
        this.fareRateProvider = fareRateProvider;
    }

    @Override
    public Fare calculate(TripDetails tripDetails) {
        Rate rate = this.fareRateProvider.provide(FareCalculationStrategyType.LUXURY, tripDetails.vehicleType());
        long durationMinutes = Duration.between(tripDetails.duration(), Instant.now()).toMinutes();
        BigDecimal amount = rate.baseFare()
                .add(rate.perKmRate().multiply(BigDecimal.valueOf(tripDetails.distance())))
                .add(rate.perMinuteRate().multiply(BigDecimal.valueOf(durationMinutes)));
        return new Fare(amount, Currency.getInstance("INR"));
    }

}
