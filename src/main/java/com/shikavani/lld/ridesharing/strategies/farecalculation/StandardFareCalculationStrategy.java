package com.shikavani.lld.ridesharing.strategies.farecalculation;

import com.shikavani.lld.ridesharing.enums.FareCalculationStrategyType;
import com.shikavani.lld.ridesharing.model.FareRateProvider;
import com.shikavani.lld.ridesharing.model.Fare;
import com.shikavani.lld.ridesharing.model.Rate;
import com.shikavani.lld.ridesharing.model.TripDetails;

import java.math.BigDecimal;
import java.util.Currency;

public class StandardFareCalculationStrategy implements FareCalculationStrategy{
    private final FareRateProvider fareRateProvider;
    public StandardFareCalculationStrategy(FareRateProvider fareRateProvider) {
        this.fareRateProvider = fareRateProvider;
    }

    @Override
    public Fare calculate(TripDetails tripDetails) {
        Rate rate = this.fareRateProvider.provide(FareCalculationStrategyType.STANDARD, tripDetails.vehicleType());
        return new Fare(rate.baseFare().add(rate.perKmRate().multiply(BigDecimal.valueOf(tripDetails.distance()))), Currency.getInstance("INR"));
    }
}
