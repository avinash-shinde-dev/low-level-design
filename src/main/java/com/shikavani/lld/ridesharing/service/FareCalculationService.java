package com.shikavani.lld.ridesharing.service;

import com.shikavani.lld.ridesharing.decorator.fare.DiscountDecorator;
import com.shikavani.lld.ridesharing.decorator.fare.TollDecorator;
import com.shikavani.lld.ridesharing.factory.FareCalculationStrategyFactory;
import com.shikavani.lld.ridesharing.model.Fare;
import com.shikavani.lld.ridesharing.model.FareRateProvider;
import com.shikavani.lld.ridesharing.model.TripDetails;
import com.shikavani.lld.ridesharing.strategies.farecalculation.FareCalculationStrategy;

import java.math.BigDecimal;
import java.util.Currency;

public class FareCalculationService {

    public Fare calculateFare(TripDetails tripDetails) {

        FareCalculationStrategy fareCalculationStrategy =FareCalculationStrategyFactory.getFareCalculationStrategy(tripDetails.fareCalculationStrategyType(), new FareRateProvider());
        // apply toll fee
        fareCalculationStrategy = new TollDecorator(fareCalculationStrategy, new Fare(BigDecimal.valueOf(100), Currency.getInstance("INR")));
        // apply discount
        fareCalculationStrategy = new DiscountDecorator(fareCalculationStrategy, 10);

        return fareCalculationStrategy.calculate(tripDetails);
    }
}
