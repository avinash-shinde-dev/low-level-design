package com.shikavani.lld.ridesharing.state;

import com.shikavani.lld.ridesharing.enums.RideStatus;
import com.shikavani.lld.ridesharing.model.Driver;
import com.shikavani.lld.ridesharing.model.Ride;

public interface RideState {
    RideStatus status();
    void request(Ride ride);
    void assignDriver(Ride ride, Driver driver);
    void reject(Ride ride);
    void driverArrived(Ride ride);
    void accept(Ride ride);
    void start(Ride ride);
    void complete(Ride ride);
    void cancel(Ride ride);
}
