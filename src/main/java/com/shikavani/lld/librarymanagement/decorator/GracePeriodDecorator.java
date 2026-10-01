package com.shikavani.lld.librarymanagement.decorator;


import com.shikavani.lld.librarymanagement.models.fine.FineBreakdown;
import com.shikavani.lld.librarymanagement.models.fine.FineDetails;


public class GracePeriodDecorator implements FineDecorator{
    private final FineDecorator fineDecorator;
    private final long gradePeriod;

    public GracePeriodDecorator(FineDecorator fineDecorator, long gradePeriod) {
        this.fineDecorator = fineDecorator;
        this.gradePeriod = gradePeriod;
    }

    @Override
    public FineBreakdown calculateFine(FineDetails details) {
        long chargeableDays = Math.max(0, details.overdueDays() - gradePeriod);
        return fineDecorator.calculateFine(details.withOverdueDays(chargeableDays));
    }
}
