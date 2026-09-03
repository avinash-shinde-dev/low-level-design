package com.shikavani.lld.ridesharing.decorator.fare;

import com.shikavani.lld.ridesharing.model.Fare;
import com.shikavani.lld.ridesharing.model.TripDetails;
import com.shikavani.lld.ridesharing.strategies.farecalculation.FareCalculationStrategy;

public class SurgeDecorator extends FareDecorator{
    private final Fare surgeFees;
    public SurgeDecorator(FareCalculationStrategy fareCalculationStrategy, Fare surgeFees) {
        super(fareCalculationStrategy);
        this.surgeFees = surgeFees;
    }

    @Override
    public Fare calculate(TripDetails tripDetails) {
        Fare fareBeforeSurge = this.decoratedFareCalculationStrategy.calculate(tripDetails);
        // Add 20 units to the same currency as standard surge, we can extend it to RainSurgeFee, etc. and calculate
        // this dynamically.
        if(!fareBeforeSurge.getCurrency().equals(surgeFees.getCurrency())){
            throw new IllegalArgumentException("Invalid currency");
        }
        Fare fareAfterSurge = new Fare();
        fareAfterSurge.setAmount(fareBeforeSurge.getAmount().add(surgeFees.getAmount()));
        fareAfterSurge.setCurrency(fareBeforeSurge.getCurrency());

        return fareAfterSurge;
    }
}
