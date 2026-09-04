package com.shikavani.lld.parkinglot.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public record ParkingLot(String parkingId, List<Floor> floors) {

    public void addFloor(Floor floor){
        this.floors.add(floor);
    }
}
