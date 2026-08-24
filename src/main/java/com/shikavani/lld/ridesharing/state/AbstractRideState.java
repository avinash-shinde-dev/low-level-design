package com.shikavani.lld.ridesharing.state;

import com.shikavani.lld.ridesharing.enums.DriverStatus;
import com.shikavani.lld.ridesharing.model.Driver;
import com.shikavani.lld.ridesharing.model.Ride;

abstract class AbstractRideState implements RideState {
    @Override public void request(Ride ride) { throw invalid("request a ride"); }
    @Override public void assignDriver(Ride ride, Driver driver) { throw invalid("assign a driver"); }
    @Override public void reject(Ride ride) { throw invalid("reject the ride"); }
    @Override public void driverArrived(Ride ride) { throw invalid("mark the driver as arrived"); }
    @Override public void accept(Ride ride) { throw invalid("accept the ride"); }
    @Override public void start(Ride ride) { throw invalid("start the ride"); }
    @Override public void complete(Ride ride) { throw invalid("complete the ride"); }
    @Override public void cancel(Ride ride) { throw invalid("cancel the ride"); }

    protected IllegalStateException invalid(String action) {
        return new IllegalStateException("Cannot " + action + " while ride is " + status());
    }

    protected void releaseDriver(Ride ride) {
        Driver driver = ride.getDriver();
        if (driver != null) driver.setStatus(DriverStatus.ONLINE);
    }

    protected void clearAssignment(Ride ride) {
        releaseDriver(ride);
        ride.setDriver(null);
        ride.setVehicle(null);
    }
}
