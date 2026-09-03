package com.shikavani.lld.ridesharing.model;

import com.shikavani.lld.ridesharing.enums.RideStatus;
import com.shikavani.lld.ridesharing.enums.VehicleType;
import com.shikavani.lld.ridesharing.observer.RideObservable;
import com.shikavani.lld.ridesharing.observer.RideObserver;
import com.shikavani.lld.ridesharing.state.RideRequestedState;
import com.shikavani.lld.ridesharing.state.RideState;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;

public class Ride implements RideObservable {

    private final String rideId;
    private Passenger passenger;
    private final Location pickup;
    private final Location drop;
    private final VehicleType vehicleType;
    private Vehicle vehicle;
    private Driver driver;
    private RideState rideState;
    private TripDetails tripDetails;
    private Rating driverRating;
    private Rating passengerRating;
    private final Set<String> rejectedDriverIds;
    private final List<RideObserver> observers;

    public Ride(Passenger passenger, Location pickup, Location drop, VehicleType vehicleType) {
        this.rideId = UUID.randomUUID().toString();
        this.passenger = passenger;
        this.pickup = pickup;
        this.drop = drop;
        this.vehicleType = vehicleType;
        rideState = new RideRequestedState();
        this.rejectedDriverIds = new HashSet<>();
        this.observers = new ArrayList<>();
    }

    @Override
    public void addObserver(RideObserver observer) {
        this.observers.add(observer);
    }

    @Override
    public void removeObserver(RideObserver observer) {
        this.observers.remove(observer);
    }

    @Override
    public void notifyObservers(RideStateChangeEvent event) {
        for (RideObserver observer : observers) {
            observer.onRideStateChanged(event);
        }
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

    public Rating getDriverRating() {
        return driverRating;
    }

    public void setDriverRating(Rating driverRating) {
        System.out.println("Rating for Driver: " + driverRating.value());
        this.driverRating = driverRating;
    }

    public Rating getPassengerRating() {
        return passengerRating;
    }

    public void setPassengerRating(Rating passengerRating) {
        System.out.println("Rating for Passenger: " + passengerRating.value());
        this.passengerRating = passengerRating;
    }

    public void rejectDriver(Driver driver){
        RideStatus oldStatus = this.rideState.status();
        RideState nextState = this.rideState.reject(this);
        this.rejectedDriverIds.add(driver.getUserId());
        this.driver = null;
        this.vehicle = null;
        this.rideState = nextState;
        notifyObservers(new RideStateChangeEvent(this, oldStatus, this.rideState.status(), Instant.now()));
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
        RideStatus oldStatus = this.rideState.status();
        RideState nextState = this.rideState.assignDriver(this, driver);
        this.driver = driver;
        this.vehicle = driver.getVehicle();
        this.rideState = nextState;
        notifyObservers(new RideStateChangeEvent(this, oldStatus, this.rideState.status(), Instant.now()));
    }

    public void arrived(){ transition( s -> s.driverArrived(this));}

    public void accept(){transition(s -> s.accept(this));}

    public void start(){ transition(s -> s.start(this));}

    public void complete(){ transition(s -> s.complete(this));}

    public void cancelled(){ transition(s -> s.cancel(this));}

    private void transition(Function<RideState, RideState> transitionFn){
        RideStatus oldStatus = this.rideState.status();
        this.rideState = transitionFn.apply(this.rideState);
        notifyObservers(new RideStateChangeEvent(this, oldStatus, this.rideState.status(), Instant.now()));
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
