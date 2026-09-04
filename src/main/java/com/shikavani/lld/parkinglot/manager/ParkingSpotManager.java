package com.shikavani.lld.parkinglot.service;

import com.shikavani.lld.parkinglot.model.ParkingRequest;
import com.shikavani.lld.parkinglot.model.ParkingSpot;
import com.shikavani.lld.parkinglot.strategy.spotassignment.SpotAssignmentStrategy;

import java.util.Optional;

public class ParkingSpotManager {
    private final SpotAssignmentStrategy spotAssignmentStrategy;

    public ParkingSpotManager(SpotAssignmentStrategy spotAssignmentStrategy) {
        this.spotAssignmentStrategy = spotAssignmentStrategy;
    }

    // find the available spot based on strategy
    public Optional<ParkingSpot> findAvailableParkingSpot(ParkingRequest parkingRequest){
        return this.spotAssignmentStrategy.findSpot(parkingRequest);
    }
}
