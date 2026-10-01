package com.shikavani.lld.librarymanagement.decorator;

import com.shikavani.lld.librarymanagement.models.fine.FineBreakdown;
import com.shikavani.lld.librarymanagement.models.fine.FineDetails;
import com.shikavani.lld.librarymanagement.models.fine.LineItem;

import java.math.BigDecimal;
import java.util.List;

public class BaseFineDecorator implements FineDecorator{

    @Override
    public FineBreakdown calculateFine(FineDetails details) {
        BigDecimal amount =  details.dailyRate()
                .multiply(BigDecimal.valueOf(details.overdueDays()))
                .multiply(details.tierMultiplier());

        return new FineBreakdown(amount, List.of(new LineItem("Base Fine", amount)));

    }
}
