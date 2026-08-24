package com.shikavani.lld.ridesharing.state;

import com.shikavani.lld.ridesharing.enums.DriverStatus;
import com.shikavani.lld.ridesharing.enums.RideStatus;
import com.shikavani.lld.ridesharing.model.Ride;

public final class DriverAssignedState extends AbstractRideState {
    @Override
    public RideStatus status() {
        return RideStatus.DRIVER_ASSIGNED;
    }

    @Override
    public void accept(Ride ride) {
        ride.getDriver().setStatus(DriverStatus.BUSY);
        ride.next(new RideAcceptedState());
    }

    @Override
    public void reject(Ride ride) {
        clearAssignment(ride);
        ride.next(new RideRequestedState());
    }

    @Override
    public void cancel(Ride ride) {
        releaseDriver(ride);
        ride.next(new RideCancelledState());
    }
}
