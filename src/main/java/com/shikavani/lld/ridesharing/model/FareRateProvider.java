package com.shikavani.lld.ridesharing.model;

import com.shikavani.lld.ridesharing.enums.FareCalculationStrategyType;
import com.shikavani.lld.ridesharing.enums.VehicleType;

import java.math.BigDecimal;
import java.util.Map;

public class FareRateProvider {

    private final static Map<FareCalculationStrategyType, Map<VehicleType, Rate>> FARE_RATE_PROVIDER_MAP = Map.of(

                FareCalculationStrategyType.STANDARD, Map.of(
                        VehicleType.TWO_WHEELER,
                        new Rate(
                                BigDecimal.valueOf(20),
                                BigDecimal.valueOf(8)
                        ),

                        VehicleType.HATCHBACK,
                        new Rate(
                                BigDecimal.valueOf(40),
                                BigDecimal.valueOf(12)
                        ),

                        VehicleType.SEDAN,
                        new Rate(
                                BigDecimal.valueOf(50),
                                BigDecimal.valueOf(15)
                        ),

                        VehicleType.SUV,
                        new Rate(
                                BigDecimal.valueOf(70),
                                BigDecimal.valueOf(20)
                        )
                ),

                FareCalculationStrategyType.SHARED, Map.of(
                        VehicleType.TWO_WHEELER,
                        new Rate(
                                BigDecimal.valueOf(10),
                                BigDecimal.valueOf(5)
                        ),

                        VehicleType.HATCHBACK,
                        new Rate(
                                BigDecimal.valueOf(20),
                                BigDecimal.valueOf(8)
                        ),

                        VehicleType.SEDAN,
                        new Rate(
                                BigDecimal.valueOf(25),
                                BigDecimal.valueOf(10)
                        ),

                        VehicleType.SUV,
                        new Rate(
                                BigDecimal.valueOf(35),
                                BigDecimal.valueOf(14)
                        )
                ),

                FareCalculationStrategyType.LUXURY, Map.of(

                        VehicleType.HATCHBACK,
                        new Rate(
                                BigDecimal.valueOf(80),
                                BigDecimal.valueOf(25)
                        ),

                        VehicleType.SEDAN,
                        new Rate(
                                BigDecimal.valueOf(100),
                                BigDecimal.valueOf(30)
                        ),

                        VehicleType.SUV,
                        new Rate(
                                BigDecimal.valueOf(120),
                                BigDecimal.valueOf(40)
                        )
                )
        );

    public Rate provide(FareCalculationStrategyType fareCalculationStrategyType, VehicleType vehicleType) {
        return FARE_RATE_PROVIDER_MAP.get(fareCalculationStrategyType).get(vehicleType);
    }
}
