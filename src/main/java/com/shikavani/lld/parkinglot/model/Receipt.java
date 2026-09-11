package com.shikavani.lld.parkinglot.model;

import java.time.Instant;

public record Receipt(String receiptId, String userId, String registrationNumber, Fee fee,  Instant exitTime) {
}
