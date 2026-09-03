package com.shikavani.lld.ridesharing.strategies.farecalculation;

import com.shikavani.lld.ridesharing.enums.FareCalculationStrategyType;
import com.shikavani.lld.ridesharing.model.FareRateProvider;
import com.shikavani.lld.ridesharing.model.Fare;
import com.shikavani.lld.ridesharing.model.Rate;
import com.shikavani.lld.ridesharing.model.TripDetails;

import java.math.BigDecimal;
import java.util.Currency;

public class SharedFareCalculationStrategy implements FareCalculationStrategy {
    private static final BigDecimal SHARED_DISCOUNT_FACTOR = BigDecimal.valueOf(0.8);  // 20 % discount
    private final FareRateProvider fareRateProvider;
    public SharedFareCalculationStrategy(FareRateProvider fareRateProvider) {
        this.fareRateProvider = fareRateProvider;
    }
    @Override
    public Fare calculate(TripDetails tripDetails) {
        Rate rate = this.fareRateProvider.provide(FareCalculationStrategyType.SHARED, tripDetails.vehicleType());
        BigDecimal amountPerPerson = rate.baseFare().add(rate.perKmRate().multiply(BigDecimal.valueOf(tripDetails.distance())));
        BigDecimal amount = amountPerPerson.multiply(SHARED_DISCOUNT_FACTOR);
        return new Fare(amount, Currency.getInstance("INR"));
    }

}
