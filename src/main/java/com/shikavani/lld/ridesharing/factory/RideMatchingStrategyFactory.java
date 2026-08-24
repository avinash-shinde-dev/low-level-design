package com.shikavani.lld.ridesharing.factory;

import com.shikavani.lld.ridesharing.enums.RideMatchingStrategyType;
import com.shikavani.lld.ridesharing.model.HaversineDistanceCalculator;
import com.shikavani.lld.ridesharing.strategies.ridematching.NearestAvailableDriverAssignmentStrategy;
import com.shikavani.lld.ridesharing.strategies.ridematching.RideMatchingStrategy;

public final class RideMatchingStrategyFactory {

    private RideMatchingStrategyFactory() {}
    public static RideMatchingStrategy getRideMatchingStrategy(RideMatchingStrategyType strategy){
        return switch (strategy) {
            case RideMatchingStrategyType.NEAREST_AVAILABLE -> new NearestAvailableDriverAssignmentStrategy(new HaversineDistanceCalculator());
        };
    }
}
