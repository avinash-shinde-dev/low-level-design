package com.shikavani.lld.parkinglot.strategy.spotassignment;

import com.shikavani.lld.parkinglot.model.ParkingSpot;

import java.util.List;
import java.util.Optional;

public class NearestAvailableParkingSpotAssignmentStrategy implements SpotAssignmentStrategy{
    @Override
    public Optional<ParkingSpot> findSpot(List<ParkingSpot> availableSpots) {
        return availableSpots.stream().findFirst();
    }
}
