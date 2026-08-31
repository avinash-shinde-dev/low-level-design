package com.shikavani.lld.ridesharing.model;

public record Rating(Integer value) {
    public Rating {
        if(value < 1 || value > 5) {
            throw new IllegalArgumentException("Invalid rating");
        }
    }
}
