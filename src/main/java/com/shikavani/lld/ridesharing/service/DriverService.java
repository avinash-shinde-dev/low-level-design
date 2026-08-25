package com.shikavani.lld.ridesharing.service;

import com.shikavani.lld.ridesharing.enums.DriverStatus;
import com.shikavani.lld.ridesharing.model.Driver;
import com.shikavani.lld.ridesharing.model.Ride;
import com.shikavani.lld.ridesharing.repository.DriverRepository;

import java.util.List;

public class DriverService {
    private final DriverRepository driverRepository;
    private final RideService rideService;

    public DriverService(DriverRepository driverRepository, RideService rideService) {
        this.driverRepository = driverRepository;
        this.rideService = rideService;
    }

    public void addDriver(Driver driver){
        this.driverRepository.save(driver);
    }

    public List<Driver> getAvailableDrivers() {
        return this.driverRepository.findAll().stream()
                .filter(d -> d.getStatus().equals(DriverStatus.ONLINE))
                .toList();
    }

    public void accept(Ride ride){
         rideService.acceptRide(ride);
    }

    public void reject(Ride ride){
        rideService.rejectRide(ride);
    }
}
