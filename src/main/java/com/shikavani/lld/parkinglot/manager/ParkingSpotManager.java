package com.shikavani.lld.parkinglot.manager;

import com.shikavani.lld.parkinglot.enums.Size;
import com.shikavani.lld.parkinglot.exception.InvalidTicketException;
import com.shikavani.lld.parkinglot.model.Floor;
import com.shikavani.lld.parkinglot.model.ParkingSpot;
import com.shikavani.lld.parkinglot.model.Ticket;
import com.shikavani.lld.parkinglot.repository.ParkingRepository;
import com.shikavani.lld.parkinglot.strategy.spotassignment.SpotAssignmentStrategy;
import java.util.List;
import java.util.Optional;

public final class ParkingSpotManager {
    private final SpotAssignmentStrategy spotAssignmentStrategy;
    private final ParkingRepository parkingRepository;

    public ParkingSpotManager(SpotAssignmentStrategy spotAssignmentStrategy, ParkingRepository parkingRepository) {
        this.spotAssignmentStrategy = spotAssignmentStrategy;
        this.parkingRepository = parkingRepository;
    }

    // find the available spot based on strategy
    public Optional<ParkingSpot> findAvailableParkingSpot(String parkingLotId, Size requiredSize) {
        List<ParkingSpot> availableSpots = findAllAvailableSpots(parkingLotId, requiredSize);
        return this.spotAssignmentStrategy.findSpot(availableSpots);
    }

    public Optional<ParkingSpot> findParkingSpot(Ticket ticket) {

        Floor floor = this.parkingRepository
                .findById((ticket.getSpot().getParkingLotId()))
                .floors()
                .stream()
                .filter(f -> f.floorId().equals(ticket.getSpot().getFloorId()))
                .findFirst()
                .orElseThrow(() -> new InvalidTicketException("Invalid ticket details"));

        return floor.parkingSpotMap()
                .get(ticket.getSpot().getSpotSize())
                .stream()
                .filter(parkingSpot -> parkingSpot.getSpotId().equals(ticket.getSpot().getSpotId()))
                .findFirst();
    }

    public List<ParkingSpot> findAllAvailableSpots(String parkingLotId, Size requiredSize) {
        return this.parkingRepository
                .findById(parkingLotId)
                .floors()
                .stream()
                .map(floor -> floor.parkingSpotMap().getOrDefault(requiredSize, List.of()))
                .flatMap(List::stream)
                .filter(ParkingSpot::isAvailable)
                .toList();
    }
}
