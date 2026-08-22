package com.shikavani.lld.ridesharing.strategies.ridematching;

import com.shikavani.lld.ridesharing.model.DistanceCalculator;
import com.shikavani.lld.ridesharing.model.Driver;
import com.shikavani.lld.ridesharing.model.Ride;
import com.shikavani.lld.ridesharing.service.DriverManager;

import java.util.*;

public class NearestAvailableDriverAssignmentStrategy implements RideMatchingStrategy{

    private final DistanceCalculator distanceCalculator;
    public NearestAvailableDriverAssignmentStrategy(DistanceCalculator distanceCalculator) {
        this.distanceCalculator = distanceCalculator;
    }

    @Override
    public Optional<Driver> match(Ride ride) {
        // 1. find all the online drivers
        // 2. filter the online drivers with requested vehicle type
        // 3. Calculate the distance between the passenger's pickup location and each driver's current location.
        // 4. Select the driver with the minimum distance.
        return DriverManager.getInstance().getAvailableDrivers()
                .stream()
                .filter(driver -> driver.getVehicle().getVehicleType().equals(ride.getVehicleType()))
                .min(Comparator.comparingDouble(driver -> distanceCalculator.calculate(
                                driver.getLocation(),
                                ride.getPickup()
                        )
                ));
    }
}
