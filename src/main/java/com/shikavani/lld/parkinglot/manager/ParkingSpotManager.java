package com.shikavani.lld.parkinglot.manager;

import com.shikavani.lld.parkinglot.enums.Size;
import com.shikavani.lld.parkinglot.model.ParkingSpot;
import com.shikavani.lld.parkinglot.repository.ParkingRepository;
import com.shikavani.lld.parkinglot.strategy.spotassignment.SpotAssignmentStrategy;

import java.util.List;
import java.util.Optional;

public class ParkingSpotManager {
    private final ParkingRepository parkingRepository;
    private final SpotAssignmentStrategy spotAssignmentStrategy;

    public ParkingSpotManager(SpotAssignmentStrategy spotAssignmentStrategy, ParkingRepository parkingRepository) {
        this.spotAssignmentStrategy = spotAssignmentStrategy;
        this.parkingRepository = parkingRepository;
    }

    public Optional<ParkingSpot> findAvailableParkingSpot(String parkingLotId, Size size){

        List<ParkingSpot> parkingSpots = this.parkingRepository.findAll()
                .stream()
                .flatMap(parkingLot -> parkingLot.floors().stream())
                .map(floor -> floor.parkingSpotMap().getOrDefault(size, List.of()))
                .flatMap(List::stream)
                .filter(ParkingSpot::isAvailable)
                .toList();

        return this.spotAssignmentStrategy.findSpot(parkingSpots);
    }

}
