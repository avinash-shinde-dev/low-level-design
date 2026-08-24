package com.shikavani.lld.ridesharing.repository;

import com.shikavani.lld.ridesharing.model.Ride;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RideRepository implements InMemoryRepository<Ride>{
    private final Map<String, Ride> rideMap = new HashMap<>();
    @Override
    public void save(Ride ride) {
        rideMap.putIfAbsent(ride.getRideId(), ride);
    }

    @Override
    public Ride findById(String id) {
        return rideMap.get(id);
    }

    @Override
    public List<Ride> findAll() {
        return this.rideMap.values().stream().toList();
    }
}
