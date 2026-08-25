package com.shikavani.lld.ridesharing.state;

import com.shikavani.lld.ridesharing.enums.RideStatus;
import com.shikavani.lld.ridesharing.model.Driver;
import com.shikavani.lld.ridesharing.model.Ride;

public interface RideState {
    RideStatus status();
    RideState request(Ride ride);
    RideState assignDriver(Ride ride, Driver driver);
    RideState reject(Ride ride);
    RideState driverArrived(Ride ride);
    RideState accept(Ride ride);
    RideState start(Ride ride);
    RideState complete(Ride ride);
    RideState cancel(Ride ride);
}
