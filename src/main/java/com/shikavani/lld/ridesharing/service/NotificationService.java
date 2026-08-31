package com.shikavani.lld.ridesharing.service;

import com.shikavani.lld.ridesharing.model.Ride;
import com.shikavani.lld.ridesharing.model.RideStateChangeEvent;
import com.shikavani.lld.ridesharing.observer.RideObserver;

public class NotificationService implements RideObserver {
    @Override
    public void onRideStateChanged(RideStateChangeEvent event) {
        Ride ride = event.ride();
        switch (event.newState()) {
            case DRIVER_ASSIGNED -> {
                notifyPassenger(ride, "Driver has been assigned");
                notifyDriver(ride, "You have been assigned a ride");
            }
            case DRIVER_ACCEPTED -> {
                notifyPassenger(ride, "Driver accepted your ride");
            }
            case DRIVER_ARRIVED -> {
                notifyPassenger(ride, "Your driver has arrived");
            }
            case RIDE_STARTED -> {
                notifyPassenger(ride, "Your ride has started");
                notifyDriver(ride, "Ride started");
            }
            case RIDE_COMPLETED -> {
                notifyPassenger(ride, "Your ride has been completed");
                notifyDriver(ride, "Ride completed");
            }
            case RIDE_CANCELLED -> {
                notifyPassenger(ride, "Your ride has been cancelled");
                if (ride.getDriver() != null) {
                    notifyDriver(ride, "Ride has been cancelled");
                }
            }
        }
    }

    private void notifyPassenger(Ride ride, String message) {
        System.out.println("Passenger (" + ride.getPassenger().getName() + "): \"" + message + "\"");
    }

    private void notifyDriver(Ride ride, String message) {
        if (ride.getDriver() != null) {
            System.out.println("Driver (" + ride.getDriver().getName() + "): \"" + message + "\"");
        }
    }
}
