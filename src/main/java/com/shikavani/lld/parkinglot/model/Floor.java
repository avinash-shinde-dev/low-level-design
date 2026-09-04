package com.shikavani.lld.parkinglot.model;

import com.shikavani.lld.parkinglot.enums.Size;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public record Floor(String floorId, Integer level, Map<Size, List<ParkingSpot>> parkingSpotMap) {

    public void addParkingSpot(ParkingSpot parkingSpot){
        parkingSpotMap.computeIfAbsent(
                parkingSpot.getSpotSize(),
                key -> new ArrayList<>()
        ).add(parkingSpot);
    }
}
