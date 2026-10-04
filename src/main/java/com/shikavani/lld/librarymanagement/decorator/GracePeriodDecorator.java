package com.shikavani.lld.librarymanagement.decorator;

import com.shikavani.lld.librarymanagement.models.fine.FineBreakdown;
import com.shikavani.lld.librarymanagement.models.fine.FineDetails;

/** The first N overdue days are free: the rule inside only sees the days after the grace period. */
public class GracePeriodDecorator implements FineDecorator {
    private final FineDecorator inner;
    private final long gracePeriodDays;

    public GracePeriodDecorator(FineDecorator inner, long gracePeriodDays) {
        this.inner = inner;
        this.gracePeriodDays = gracePeriodDays;
    }

    @Override
    public FineBreakdown calculateFine(FineDetails details) {
        long chargeableDays = Math.max(0, details.overdueDays() - gracePeriodDays);
        return inner.calculateFine(details.withOverdueDays(chargeableDays));
    }
}
