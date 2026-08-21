package com.shikavani.lld.ridesharing.model;

import com.shikavani.lld.ridesharing.enums.RideStatus;
import com.shikavani.lld.ridesharing.enums.VehicleType;
import com.shikavani.lld.ridesharing.state.*;
import java.util.UUID;

public class Ride {

    private final String rideId;
    private Passenger passenger;
    private final Location pickup;
    private final Location drop;
    private final VehicleType vehicleType;
    private Vehicle vehicle;
    private Driver driver;
    private RideState rideState;

    public Ride(Passenger passenger, Location pickup, Location drop, VehicleType vehicleType) {
        this.rideId = UUID.randomUUID().toString();
        this.passenger = passenger;
        this.pickup = pickup;
        this.drop = drop;
        this.vehicleType = vehicleType;
        rideState = new RequestRideState();
    }

    public Location getPickup() {
        return pickup;
    }

    public Location getDrop() {
        return drop;
    }

    public String getRideId() {
        return rideId;
    }

    public Passenger getPassenger() {
        return passenger;
    }

    public void setPassenger(Passenger passenger) {
        this.passenger = passenger;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle){
        this.vehicle = vehicle;
    }
    public Driver getDriver() {
        return driver;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }

    public RideStatus getStatus(){
        return rideState.status();
    }

    protected void next(RideState state){
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
