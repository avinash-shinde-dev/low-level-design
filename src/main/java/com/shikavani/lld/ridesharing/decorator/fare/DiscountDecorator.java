package com.shikavani.lld.ridesharing.decorator.fare;

import com.shikavani.lld.ridesharing.model.Fare;
import com.shikavani.lld.ridesharing.model.TripDetails;
import com.shikavani.lld.ridesharing.strategies.farecalculation.FareCalculationStrategy;

import java.math.BigDecimal;

public class DiscountDecorator extends FareDecorator{

    private final Integer discountPercentage;

    public DiscountDecorator(FareCalculationStrategy fareCalculationStrategy, Integer discountPercentage) {
        super(fareCalculationStrategy);
        this.discountPercentage = discountPercentage;
    }

    @Override
    public Fare calculate(TripDetails tripDetails) {

        // For simplicity, we are considering 10% discount, but that can also be configurable.
        Fare fareBeforeDiscount = this.decoratedFareCalculationStrategy.calculate(tripDetails);

        Fare fareAfterDiscount = new Fare();
        fareAfterDiscount.setAmount(fareBeforeDiscount.getAmount().multiply(BigDecimal.valueOf(1 - (double)discountPercentage/100)));
        fareAfterDiscount.setCurrency(fareBeforeDiscount.getCurrency());

        return fareAfterDiscount;
    }
}
