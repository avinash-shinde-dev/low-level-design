package com.shikavani.lld.ridesharing.strategies.farecalculation;

import com.shikavani.lld.ridesharing.model.Fare;
import com.shikavani.lld.ridesharing.model.TripDetails;

public interface FareCalculationStrategy {
    Fare calculate(TripDetails tripDetails);
}
