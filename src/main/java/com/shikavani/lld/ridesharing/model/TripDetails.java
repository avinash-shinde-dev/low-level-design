package com.shikavani.lld.ridesharing.model;

import com.shikavani.lld.ridesharing.enums.FareCalculationStrategyType;
import com.shikavani.lld.ridesharing.enums.VehicleType;
import java.time.Instant;

public record TripDetails(Double distance, Instant duration, VehicleType vehicleType, FareCalculationStrategyType fareCalculationStrategyType) { }
