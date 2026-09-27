package com.shikavani.lld.librarymanagement.models.fine;

import java.math.BigDecimal;

public record FineDetails(
        long overdueDays,
        BigDecimal dailyRate,
        BigDecimal tierMultiplier,
        BigDecimal bookPrice) {        // needed by the cap decorator

    public FineDetails withOverdueDays(long days) {
        return new FineDetails(days, dailyRate, tierMultiplier, bookPrice);
    }
}
