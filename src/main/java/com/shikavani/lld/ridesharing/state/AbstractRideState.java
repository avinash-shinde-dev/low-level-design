package com.shikavani.lld.ridesharing.state;

import com.shikavani.lld.ridesharing.enums.DriverStatus;
import com.shikavani.lld.ridesharing.model.Driver;
import com.shikavani.lld.ridesharing.model.Ride;

abstract class AbstractRideState implements RideState {
    @Override public RideState request(Ride ride) { throw invalid("request a ride"); }
    @Override public RideState assignDriver(Ride ride, Driver driver) { throw invalid("assign a driver"); }
    @Override public RideState reject(Ride ride) { throw invalid("reject the ride"); }
    @Override public RideState driverArrived(Ride ride) { throw invalid("mark the driver as arrived"); }
    @Override public RideState accept(Ride ride) { throw invalid("accept the ride"); }
    @Override public RideState start(Ride ride) { throw invalid("start the ride"); }
    @Override public RideState complete(Ride ride) { throw invalid("complete the ride"); }
    @Override public RideState cancel(Ride ride) { throw invalid("cancel the ride"); }

    protected IllegalStateException invalid(String action) {
        return new IllegalStateException("Cannot " + action + " while ride is " + status());
    }

    protected void releaseDriver(Ride ride) {
        Driver driver = ride.getDriver();
        if (driver != null) driver.setStatus(DriverStatus.ONLINE);
        System.out.println("Driver has been released.");
    }
}
