package com.shikavani.lld.ridesharing.decorator.fare;

import com.shikavani.lld.ridesharing.model.Fare;
import com.shikavani.lld.ridesharing.model.TripDetails;
import com.shikavani.lld.ridesharing.strategies.farecalculation.FareCalculationStrategy;

public class TollDecorator extends FareDecorator{
    private final Fare tollFees;
    public TollDecorator(FareCalculationStrategy fareCalculationStrategy, Fare tollFees) {
        super(fareCalculationStrategy);
        this.tollFees = tollFees;
    }

    @Override
    public Fare calculate(TripDetails tripDetails) {
        // For simplicity, we are considering 100 units of toll fee, but that can also be configurable.
        Fare fareBeforeToll = this.decoratedFareCalculationStrategy.calculate(tripDetails);

        // validation
        if(fareBeforeToll.getCurrency() != tollFees.getCurrency()){
            throw new IllegalArgumentException("Invalid currency");
        }
        Fare fareAfterToll = new Fare();
        fareAfterToll.setAmount(fareBeforeToll.getAmount().add(tollFees.getAmount()));
        fareAfterToll.setCurrency(fareBeforeToll.getCurrency());

        return fareAfterToll;
    }
}
