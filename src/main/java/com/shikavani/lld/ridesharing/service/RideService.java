package com.shikavani.lld.ridesharing.service;

import com.shikavani.lld.ridesharing.enums.DriverStatus;
import com.shikavani.lld.ridesharing.enums.RideMatchingStrategyType;
import com.shikavani.lld.ridesharing.enums.VehicleType;
import com.shikavani.lld.ridesharing.factory.RideMatchingStrategyFactory;
import com.shikavani.lld.ridesharing.model.*;
import com.shikavani.lld.ridesharing.observer.RideObserver;
import com.shikavani.lld.ridesharing.repository.DriverRepository;
import com.shikavani.lld.ridesharing.repository.RideRepository;
import com.shikavani.lld.ridesharing.strategies.ridematching.RideMatchingStrategy;

import java.util.List;

public class RideService {

    private final RideMatchingStrategy rideMatchingStrategy;
    private final RideRepository rideRepository;
    private final FareCalculationService fareCalculationService;
    private final List<RideObserver> rideObservers;

    public RideService(RideRepository rideRepository, DriverRepository driverRepository, FareCalculationService fareCalculationService, List<RideObserver> rideObservers) {
        this.rideMatchingStrategy = RideMatchingStrategyFactory.getRideMatchingStrategy(RideMatchingStrategyType.NEAREST_AVAILABLE, driverRepository);
        this.rideRepository = rideRepository;
        this.fareCalculationService = fareCalculationService;
        this.rideObservers = rideObservers;
    }

    public Ride requestRide(Passenger passenger, Location pickup, Location drop, VehicleType vehicleType ){
        // create the ride
        Ride ride = new Ride(passenger, pickup, drop, vehicleType);
        rideObservers.forEach(rideObserver -> ride.addObserver(rideObserver));
        ride.requestRide();
        // Assign the driver
        attemptDriverAssignment(ride);
        // save the ride
        rideRepository.save(ride);
        return ride;
    }

    public void acceptRide(Ride ride){
         ride.accept();
    }

    public void arrive(Ride ride){
        ride.arrived();
    }

    public void startRide(Ride ride){
        ride.start();
    }

    public void completeRide(Ride ride){
        ride.complete();
    }

    public void cancelRide(Ride ride){
        ride.cancelled();
    }

    public void rejectRide(Ride ride){
        ride.rejectDriver(ride.getDriver());
        attemptDriverAssignment(ride);
    }

    private void attemptDriverAssignment(Ride ride) {
        this.rideMatchingStrategy.match(ride)
                .ifPresent(driver -> {
                    driver.setStatus(DriverStatus.BUSY);
                    ride.assignDriver(driver);
                });
    }

    public Fare calcuateFare(TripDetails tripDetails){
        return this.fareCalculationService.calculateFare(tripDetails);
    }


}
