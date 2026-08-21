package com.shikavani.lld.ridesharing.model;

import com.shikavani.lld.ridesharing.state.*;

public class Ride {
    private final Location pickup;
    private final Location drop;
    private final Vehicle vehicle;
    private Driver driver;
    private RideState rideState;
    private boolean isCancelled;

    public Ride(Location pickup, Location drop, Vehicle vehicle) {
        this.pickup = pickup;
        this.drop = drop;
        this.vehicle = vehicle;
        rideState = new RequestRideState();
    }

    public Location getPickup() {
        return pickup;
    }

    public Location getDrop() {
        return drop;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public Driver getDriver() {
        return driver;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }

    public boolean isCancelled() {
        return isCancelled;
    }

    public void setCancelled(boolean cancelled) {
        isCancelled = cancelled;
    }

    public void next(RideState state){
        this.rideState = state;
    }

    public void requestRide(){
        this.rideState.requestRide(this);
        this.next(new AssignedDriverState());
    }

    public void assignDriver(Driver driver){
        this.rideState.assignDriver(this, driver);
        this.next(new AcceptState());
    }

    public void accept(){
        this.rideState.accept(this);
        this.next(new StartState());
    }

    public void reject(){
        this.rideState.reject(this);
        this.next(new AssignedDriverState());
    }

    public void start(){
        this.rideState.start(this);
        this.next(new CompleteState());
    }

    public void complete(){
        this.rideState.complete(this);
        this.next(new RequestRideState());
    }

    public void cancelled(){
        this.rideState.cancelled(this);
        this.next(new RequestRideState());
    }
}
