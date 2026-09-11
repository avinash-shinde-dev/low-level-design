package com.shikavani.lld.parkinglot.model;

import com.shikavani.lld.parkinglot.enums.Size;

import java.util.List;
import java.util.Map;

public class ParkingSpot {

    private final String parkingLotId;
    private final String floorId;
    private final String spotId;
    private final Size spotSize;
    private boolean isAvailable;
    private Vehicle parkedVehicle;
    private static final Map<Size, List<Size>> canFitSize = Map.of(
    Size.SMALL, List.of(Size.SMALL),
    Size.COMPACT, List.of(Size.SMALL, Size.COMPACT),
    Size.MEDIUM, List.of(Size.SMALL, Size.COMPACT, Size.MEDIUM),
    Size.LARGE, List.of(Size.SMALL, Size.COMPACT, Size.MEDIUM, Size.LARGE));

    public ParkingSpot(String parkingLotId, String floorId, String spotId, Size spotSize) {
        this.parkingLotId = parkingLotId;
        this.floorId = floorId;
        this.spotId = spotId;
        this.spotSize = spotSize;
        this.isAvailable = true;
    }

    public String getSpotId() {
        return spotId;
    }

    public Size getSpotSize() {
        return spotSize;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    public Vehicle getParkedVehicle() {
        return parkedVehicle;
    }

    public void setParkedVehicle(Vehicle parkedVehicle) {
        this.parkedVehicle = parkedVehicle;
    }

    public String getParkingLotId() {
        return parkingLotId;
    }

    public String getFloorId() {
        return floorId;
    }

    public void park(Vehicle vehicle){
        this.setAvailable(false);
        this.setParkedVehicle(vehicle);
    }

    public void unPark(){
        this.setAvailable(true);
        this.setParkedVehicle(null);
    }

    public boolean canFit(Vehicle vehicle){
        return canFitSize.get(this.getSpotSize()).contains(vehicle.getSize());
    }

    @Override
    public String toString() {
        return "ParkingSpot{" +
                "parkingLotId='" + parkingLotId + '\'' +
                ", floorId='" + floorId + '\'' +
                ", spotId='" + spotId + '\'' +
                ", spotSize=" + spotSize +
                ", isAvailable=" + isAvailable +
                ", parkedVehicle=" + parkedVehicle +
                '}';
    }
}
