package com.shikavani.lld.parkinglot.model;

public record User(String id, String name, String email, String phoneNo, Vehicle vehicle) {

    public User {
        if(phoneNo.length() < 0 || phoneNo.length() > 10){
            throw new IllegalArgumentException("Invalid phone number.");
        }
    }
}
