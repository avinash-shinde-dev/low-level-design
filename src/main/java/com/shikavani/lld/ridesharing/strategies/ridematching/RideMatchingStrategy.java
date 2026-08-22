package com.shikavani.lld.ridesharing.strategies.ridematching;

import com.shikavani.lld.ridesharing.model.Driver;
import com.shikavani.lld.ridesharing.model.Ride;

import java.util.Optional;

public interface RideMatchingStrategy {
    Optional<Driver> match(Ride ride);
}
