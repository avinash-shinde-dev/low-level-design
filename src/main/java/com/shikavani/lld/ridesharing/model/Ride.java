package com.shikavani.lld.ridesharing.model;

import com.shikavani.lld.ridesharing.enums.RideStatus;
import com.shikavani.lld.ridesharing.enums.VehicleType;
import com.shikavani.lld.ridesharing.state.RideRequestedState;
import com.shikavani.lld.ridesharing.state.RideState;

import java.util.HashSet;
import java.util.Set;
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
    private TripDetails tripDetails;
    private final Set<String> rejectedDriverIds;

    public Ride(Passenger passenger, Location pickup, Location drop, VehicleType vehicleType) {
        this.rideId = UUID.randomUUID().toString();
        this.passenger = passenger;
        this.pickup = pickup;
        this.drop = drop;
        this.vehicleType = vehicleType;
        rideState = new RideRequestedState();
        this.rejectedDriverIds = new HashSet<>();
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

    public Driver getDriver() {
        return driver;
    }

    public TripDetails getTripDetails(){
        return this.tripDetails;
    }

    public void setTripDetails(TripDetails tripDetails) {
        this.tripDetails = tripDetails;
    }

    public void rejectDriver(Driver driver){
        RideState nextState = this.rideState.reject(this);
        this.rejectedDriverIds.add(driver.getUserId());
        this.driver = null;
        this.vehicle = null;
        this.rideState = nextState;
    }
    public boolean wasRejectedBy(Driver driver){
        return this.rejectedDriverIds.contains(driver.getUserId());
    }

    public RideStatus getStatus(){
        return rideState.status();
    }

    public void requestRide(){
        this.rideState = this.rideState.request(this);
    }

    public void assignDriver(Driver driver){
        RideState nextState = this.rideState.assignDriver(this, driver);
        this.driver = driver;
        this.vehicle = driver.getVehicle();
        this.rideState = nextState;
    }

    public void arrived(){
        this.rideState = this.rideState.driverArrived(this);
    }

    public void accept(){
        this.rideState = this.rideState.accept(this);
    }

    public void start(){
        this.rideState = this.rideState.start(this);
    }

    public void complete(){
        this.rideState = this.rideState.complete(this);
    }

    public void cancelled(){
        this.rideState = this.rideState.cancel(this);
    }

    @Override
    public String toString() {
        return "Ride{" +
                "rideId='" + rideId + '\'' +
                ", passenger=" + passenger +
                ", pickup=" + pickup +
                ", drop=" + drop +
                ", vehicleType=" + vehicleType +
                ", vehicle=" + vehicle +
                ", driver=" + driver +
                ", rideState=" + rideState +
                '}';
    }
}
