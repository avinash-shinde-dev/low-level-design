package com.shikavani.lld.ridesharing.repository;

import com.shikavani.lld.ridesharing.enums.DriverStatus;
import com.shikavani.lld.ridesharing.model.Driver;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DriverRepository implements InMemoryRepository<Driver> {
    private final Map<String, Driver> driverMap = new HashMap<>();
    @Override
    public void save(Driver driver) {
        driverMap.putIfAbsent(driver.getUserId(), driver);
    }

    @Override
    public Driver findById(String id) {
        return driverMap.get(id);
    }

    @Override
    public List<Driver> findAll() {
        return driverMap.values().stream().toList();
    }

}
