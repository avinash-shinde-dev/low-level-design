package com.shikavani.lld.librarymanagement.models;

import java.math.BigDecimal;

/** The facts a fine rule can look at. */
public record FineDetails(long overdueDays, BigDecimal dailyRate, BigDecimal tierMultiplier, BigDecimal bookPrice) {

    public FineDetails withOverdueDays(long days) {
        return new FineDetails(days, dailyRate, tierMultiplier, bookPrice);
    }
}
