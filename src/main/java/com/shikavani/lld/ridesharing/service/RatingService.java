package com.shikavani.lld.ridesharing.service;

import com.shikavani.lld.ridesharing.enums.RideStatus;
import com.shikavani.lld.ridesharing.model.Driver;
import com.shikavani.lld.ridesharing.model.Passenger;
import com.shikavani.lld.ridesharing.model.Rating;
import com.shikavani.lld.ridesharing.model.Ride;

public class RatingService {

    public void rateDriver(Ride ride, Passenger passenger, Rating rating){
        // validate if the passenger was part of ride
        if(ride.getPassenger() != passenger){
            throw new IllegalArgumentException("Passenger was not part of ride");
        }
        validateRide(ride);
        ride.setDriverRating(rating);

    }

    public void ratePassenger(Ride ride, Driver driver, Rating rating){
        // validate if the passenger was part of ride
        if(ride.getDriver() != driver){
            throw new IllegalArgumentException("Driver was not part of ride");
        }
        validateRide(ride);
        ride.setPassengerRating(rating);

    }

    private void validateRide(Ride ride){
        if(ride.getStatus() != RideStatus.RIDE_COMPLETED){
            throw new IllegalStateException("Ride is not completed");
        }
    }
}
