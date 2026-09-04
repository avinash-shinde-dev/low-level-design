package com.shikavani.lld.parkinglot.model;

import com.shikavani.lld.parkinglot.enums.Size;

public class ParkingSpot {
    private Integer level;
    private final String spotId;
    private final Size spotSize;
    private boolean isAvailable;
    private Vehicle parkedVehicle;

    public ParkingSpot(String spotId, Size spotSize) {
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

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    @Override
    public String toString() {
        return "ParkingSpot{" +
                "spotId='" + spotId + '\'' +
                ", spotSize=" + spotSize +
                ", isAvailable=" + isAvailable +
                ", parkedVehicle=" + parkedVehicle +
                '}';
    }
}
