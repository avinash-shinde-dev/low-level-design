package com.shikavani.lld.ridesharing.decorator.fare;

import com.shikavani.lld.ridesharing.strategies.farecalculation.FareCalculationStrategy;

import java.util.Objects;

public abstract class FareDecorator implements FareCalculationStrategy {
    protected FareCalculationStrategy decoratedFareCalculationStrategy;

    public FareDecorator(FareCalculationStrategy fareCalculationStrategy) {
        this.decoratedFareCalculationStrategy = Objects.requireNonNull(fareCalculationStrategy);
    }

}
