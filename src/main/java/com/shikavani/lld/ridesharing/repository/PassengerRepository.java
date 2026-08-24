package com.shikavani.lld.ridesharing.repository;

import com.shikavani.lld.ridesharing.model.Passenger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PassengerRepository implements InMemoryRepository<Passenger> {
    private final Map<String, Passenger> passengerMap = new HashMap<>();
    @Override
    public void save(Passenger passenger) {
        passengerMap.putIfAbsent(passenger.getUserId(), passenger);
    }

    @Override
    public Passenger findById(String id) {
        return passengerMap.get(id);
    }

    @Override
    public List<Passenger> findAll() {
        return this.passengerMap.values().stream().toList();
    }
}
