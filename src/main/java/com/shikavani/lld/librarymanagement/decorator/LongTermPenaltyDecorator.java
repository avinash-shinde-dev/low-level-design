package com.shikavani.lld.librarymanagement.decorator;

import com.shikavani.lld.librarymanagement.models.fine.FineBreakdown;
import com.shikavani.lld.librarymanagement.models.fine.FineDetails;

import java.math.BigDecimal;

/**
 * Adds a flat penalty when the book is overdue by MORE than thresholdDays.
 * Put it OUTSIDE the grace period rule so it looks at the real number of overdue days.
 */
public class LongTermPenaltyDecorator implements FineDecorator {
    private final FineDecorator inner;
    private final int thresholdDays;
    private final BigDecimal penaltyAmount;

    public LongTermPenaltyDecorator(FineDecorator inner, int thresholdDays, BigDecimal penaltyAmount) {
        this.inner = inner;
        this.thresholdDays = thresholdDays;
        this.penaltyAmount = penaltyAmount;
    }

    @Override
    public FineBreakdown calculateFine(FineDetails details) {
        FineBreakdown breakdown = inner.calculateFine(details);
        if (details.overdueDays() > thresholdDays) {
            breakdown = breakdown.plus("Long-term penalty (over " + thresholdDays + " days)", penaltyAmount);
        }
        return breakdown;
    }
}
