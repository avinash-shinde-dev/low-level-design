package com.shikavani.lld.ridesharing.strategies.ridematching;

import com.shikavani.lld.ridesharing.enums.DriverStatus;
import com.shikavani.lld.ridesharing.model.DistanceCalculator;
import com.shikavani.lld.ridesharing.model.Driver;
import com.shikavani.lld.ridesharing.model.Ride;
import com.shikavani.lld.ridesharing.repository.DriverRepository;
import com.shikavani.lld.ridesharing.service.DriverService;

import java.util.*;

public class NearestAvailableDriverAssignmentStrategy implements RideMatchingStrategy{

    private final DistanceCalculator distanceCalculator;
    private final DriverRepository driverRepository;
    public NearestAvailableDriverAssignmentStrategy(DistanceCalculator distanceCalculator, DriverRepository driverRepository) {
        this.distanceCalculator = distanceCalculator;
        this.driverRepository = driverRepository;
    }

    @Override
    public Optional<Driver> match(Ride ride) {
        // 1. find all the online drivers
        // 2. filter the online drivers with requested vehicle type
        // 3. Calculate the distance between the passenger's pickup location and each driver's current location.
        // 4. Select the driver with the minimum distance.
        // 5. if the ride was rejected by any driver, then the next closest driver should be assinged
        return this.driverRepository.findAll()
                .stream()
                .filter(driver -> !ride.wasRejectedBy(driver))
                .filter(driver -> driver.getStatus().equals(DriverStatus.ONLINE))
                .filter(driver -> driver.getVehicle().getVehicleType().equals(ride.getVehicleType()))
                .min(Comparator.comparingDouble(driver -> distanceCalculator.calculate(
                                driver.getLocation(),
                                ride.getPickup()
                        )
                ));
    }
}
