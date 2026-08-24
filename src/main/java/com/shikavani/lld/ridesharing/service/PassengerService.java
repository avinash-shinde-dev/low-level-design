package com.shikavani.lld.ridesharing.service;

import com.shikavani.lld.ridesharing.enums.VehicleType;
import com.shikavani.lld.ridesharing.model.Location;
import com.shikavani.lld.ridesharing.model.Passenger;
import com.shikavani.lld.ridesharing.model.Ride;
import com.shikavani.lld.ridesharing.model.Vehicle;
import com.shikavani.lld.ridesharing.repository.PassengerRepository;

import java.util.List;

public class PassengerService {
    private final RideService rideService;
    private final PassengerRepository passengerRepository;
    public PassengerService(PassengerRepository passengerRepository, RideService rideService) {
        this.passengerRepository = passengerRepository;
        this.rideService = rideService;
    }

    public void addPassenger(Passenger passenger){
        this.passengerRepository.save(passenger);
    }

    public List<Passenger> getAllPassengers(){
        return this.passengerRepository.findAll();
    }

    public Ride requestRide(Passenger passenger, Location pickup, Location drop, VehicleType vehicleType ){
        return rideService.requestRide(passenger, pickup, drop, vehicleType);
    }

    public void cancelRide(Ride ride){
        ride.cancelled();
    }

}
