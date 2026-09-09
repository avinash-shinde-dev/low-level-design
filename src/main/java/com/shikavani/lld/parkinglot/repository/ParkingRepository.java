package com.shikavani.lld.parkinglot.repository;

import com.shikavani.lld.parkinglot.enums.Size;
import com.shikavani.lld.parkinglot.model.Floor;
import com.shikavani.lld.parkinglot.model.ParkingLot;
import com.shikavani.lld.parkinglot.model.ParkingSpot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ParkingRepository implements InMemoryRepository<String, ParkingLot> {
    private final Map<String, ParkingLot> parkingLotMap = new ConcurrentHashMap<>();

    public ParkingRepository() {
        this.setupParkingLot();
    }

    @Override
    public ParkingLot save(ParkingLot parkingLot) {
        parkingLotMap.put(parkingLot.parkingId(), parkingLot);
        return parkingLot;
    }

    @Override
    public ParkingLot findById(String parkingId) {
        return parkingLotMap.get(parkingId);
    }

    @Override
    public List<ParkingLot> findAll() {
        return parkingLotMap.values().stream().toList();
    }

    private List<ParkingSpot> generateParkingSpots(String parkingLotId, String floorId) {
        List<ParkingSpot> parkingSpots = new ArrayList<>();
        Size[] sizes = new Size[]{Size.SMALL, Size.LARGE, Size.MEDIUM, Size.COMPACT};
        for (int i = 0; i < 12; i++) {
            parkingSpots.add(new ParkingSpot(parkingLotId, floorId, "S" + i, sizes[i%4]));
        }

        return parkingSpots;
    }

    private void setupParkingLot() {

        ParkingLot parkingLot = new ParkingLot("PL1", new ArrayList<>());
        Floor floor1 = new Floor("F1", 1, new ConcurrentHashMap<>());
        Floor floor2 = new Floor("F2", 2, new ConcurrentHashMap<>());
        Floor floor3 = new Floor("F3", 3, new ConcurrentHashMap<>());

        parkingLot.addFloor(floor1);
        parkingLot.addFloor(floor2);
        parkingLot.addFloor(floor3);

        List<ParkingSpot> parkingSpotsFloor1 = generateParkingSpots(parkingLot.parkingId(),floor1.floorId());
        parkingSpotsFloor1.forEach(parkingSpot -> floor1.addParkingSpot(parkingSpot));

        List<ParkingSpot> parkingSpotsFloor2 = generateParkingSpots(parkingLot.parkingId(),floor2.floorId());
        parkingSpotsFloor2.forEach(parkingSpot -> floor2.addParkingSpot(parkingSpot));

        List<ParkingSpot> parkingSpotsFloor3 = generateParkingSpots(parkingLot.parkingId(),floor3.floorId());
        parkingSpotsFloor3.forEach(parkingSpot -> floor3.addParkingSpot(parkingSpot));

        this.parkingLotMap.put(parkingLot.parkingId(), parkingLot);
    }

}
