package com.shikavani.lld.librarymanagement.decorator;

import com.shikavani.lld.librarymanagement.models.fine.FineBreakdown;
import com.shikavani.lld.librarymanagement.models.fine.FineDetails;

import java.math.BigDecimal;

public class LongTermPenaltyDecorator implements FineDecorator{
    private final FineDecorator fineDecorator;
    private final int thresholdDays;
    private final BigDecimal penaltyAmount;

    public LongTermPenaltyDecorator(FineDecorator fineDecorator, int thresholdDays, BigDecimal penaltyAmount) {
        this.fineDecorator = fineDecorator;
        this.thresholdDays = thresholdDays;
        this.penaltyAmount = penaltyAmount;
    }

    @Override
    public FineBreakdown calculateFine(FineDetails details) {
        FineBreakdown breakdown = this.fineDecorator.calculateFine(details);
        // apply the penalties
        if(details.overdueDays() > thresholdDays){
            breakdown = breakdown.plus("Long term Penalties", penaltyAmount);
        }
        return breakdown;
    }
}
