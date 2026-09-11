package com.shikavani.lld.parkinglot.strategy.spotassignment;

import com.shikavani.lld.parkinglot.enums.Size;
import com.shikavani.lld.parkinglot.model.ParkingSpot;

import java.util.List;
import java.util.Optional;

public interface SpotAssignmentStrategy {
    Optional<ParkingSpot> findSpot(List<ParkingSpot> availableSpots);
}
