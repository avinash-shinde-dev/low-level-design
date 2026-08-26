package com.shikavani.lld.ridesharing.factory;

import com.shikavani.lld.ridesharing.enums.FareCalculationStrategyType;
import com.shikavani.lld.ridesharing.model.FareRateProvider;
import com.shikavani.lld.ridesharing.strategies.farecalculation.FareCalculationStrategy;
import com.shikavani.lld.ridesharing.strategies.farecalculation.LuxuryFareCalculationStrategy;
import com.shikavani.lld.ridesharing.strategies.farecalculation.SharedFareCalculationStrategy;
import com.shikavani.lld.ridesharing.strategies.farecalculation.StandardFareCalculationStrategy;

public final class FareCalculationStrategyFactory {
    public static FareCalculationStrategy getFareCalculationStrategy(FareCalculationStrategyType fareCalculationStrategyType, FareRateProvider fareRateProvider){
       return switch (fareCalculationStrategyType){
            case FareCalculationStrategyType.STANDARD ->  new StandardFareCalculationStrategy(fareRateProvider);
            case FareCalculationStrategyType.SHARED ->  new SharedFareCalculationStrategy(fareRateProvider);
            case FareCalculationStrategyType.LUXURY ->  new LuxuryFareCalculationStrategy(fareRateProvider);
        };
    }
}
