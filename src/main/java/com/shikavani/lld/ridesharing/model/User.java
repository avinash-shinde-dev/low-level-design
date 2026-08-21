package com.shikavani.lld.ridesharing.model;

public sealed class User permits Passenger, Driver {
    private String userId;
    private String name;
    private String email;
    private String phoneNo;
    private Location location;

    public User(String userId, String name, String email, String phoneNo, Location location) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.phoneNo = phoneNo;
        this.location = location;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId='" + userId + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phoneNo='" + phoneNo + '\'' +
                ", location=" + location +
                '}';
    }
}
