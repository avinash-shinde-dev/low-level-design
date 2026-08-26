package com.shikavani.lld.ridesharing.decorator.fare;

import com.shikavani.lld.ridesharing.model.Fare;
import com.shikavani.lld.ridesharing.model.TripDetails;
import com.shikavani.lld.ridesharing.strategies.farecalculation.FareCalculationStrategy;

import java.math.BigDecimal;

public class TaxDecorator extends FareDecorator{
    private final Integer taxPercentage;
    public TaxDecorator(FareCalculationStrategy fareCalculationStrategy, Integer taxPercentage) {
        super(fareCalculationStrategy);
        this.taxPercentage = taxPercentage;
    }

    @Override
    public Fare calculate(TripDetails tripDetails) {
        Fare fareBeforeTax = this.decoratedFareCalculationStrategy.calculate(tripDetails);

        Fare fareAfterTax = new Fare();
        fareAfterTax.setAmount(fareBeforeTax.getAmount().multiply(BigDecimal.valueOf(1 + (double) taxPercentage/100)));
        fareAfterTax.setCurrency(fareBeforeTax.getCurrency());

        return fareAfterTax;
    }
}
