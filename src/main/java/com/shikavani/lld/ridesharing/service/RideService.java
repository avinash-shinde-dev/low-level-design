package com.shikavani.lld.ridesharing.service;

import com.shikavani.lld.ridesharing.enums.DriverStatus;
import com.shikavani.lld.ridesharing.enums.RideMatchingStrategyType;
import com.shikavani.lld.ridesharing.enums.VehicleType;
import com.shikavani.lld.ridesharing.factory.RideMatchingStrategyFactory;
import com.shikavani.lld.ridesharing.model.*;
import com.shikavani.lld.ridesharing.repository.DriverRepository;
import com.shikavani.lld.ridesharing.repository.RideRepository;
import com.shikavani.lld.ridesharing.strategies.ridematching.RideMatchingStrategy;

import java.util.Optional;
import java.util.function.Consumer;

public class RideService {

    private final RideMatchingStrategy rideMatchingStrategy;
    private final RideRepository rideRepository;

    public RideService(RideRepository rideRepository, DriverService driverService) {
        this.rideMatchingStrategy = RideMatchingStrategyFactory.getRideMatchingStrategy(RideMatchingStrategyType.NEAREST_AVAILABLE, driverService);
        this.rideRepository = rideRepository;
    }

    public Ride requestRide(Passenger passenger, Location pickup, Location drop, VehicleType vehicleType ){
        // create the ride
        Ride ride = new Ride(passenger, pickup, drop, vehicleType);
        ride.requestRide();
        // Assign the driver
        Optional<Driver> driver = this.rideMatchingStrategy.match(ride);
        driver.ifPresent(d -> {
            d.setStatus(DriverStatus.BUSY);
            ride.assignDriver(d);
        });
        // save the ride
        rideRepository.save(ride);
        return ride;
    }


}
