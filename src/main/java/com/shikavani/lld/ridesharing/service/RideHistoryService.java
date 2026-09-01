package com.shikavani.lld.ridesharing.service;

import com.shikavani.lld.ridesharing.enums.RideStatus;
import com.shikavani.lld.ridesharing.model.Ride;
import com.shikavani.lld.ridesharing.model.RideStateChangeEvent;
import com.shikavani.lld.ridesharing.observer.RideObserver;
import com.shikavani.lld.ridesharing.repository.RideRepository;

import java.util.List;

public class RideHistoryService implements RideObserver {
    private final RideRepository rideRepository;

    public RideHistoryService(RideRepository rideRepository) {
        this.rideRepository = rideRepository;
    }

    public List<Ride> findAllPassengerRides(String passengerId){
        return this.rideRepository.findAll().stream()
                .filter(ride -> ride.getPassenger().getUserId().equals(passengerId))
                .filter(ride ->  ride.getStatus() == RideStatus.RIDE_COMPLETED)
                .toList();
    }

    public List<Ride> findAllDriverRides(String driverId){
        return this.rideRepository.findAll().stream()
                .filter(ride -> ride.getDriver().getUserId().equals(driverId))
                .filter(ride ->  ride.getStatus() == RideStatus.RIDE_COMPLETED)
                .toList();
    }

    @Override
    public void onRideStateChanged(RideStateChangeEvent event) {
        if(event.ride().getStatus() == RideStatus.RIDE_COMPLETED){
            this.rideRepository.save(event.ride());
        }
    }
}
