package com.shikavani.lld.librarymanagement.decorator;

import com.shikavani.lld.librarymanagement.models.fine.FineBreakdown;
import com.shikavani.lld.librarymanagement.models.fine.FineDetails;
import com.shikavani.lld.librarymanagement.models.fine.LineItem;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/** Innermost rule: overdueDays x dailyRate x tier multiplier. */
public class BaseFineDecorator implements FineDecorator {

    @Override
    public FineBreakdown calculateFine(FineDetails d) {
        BigDecimal amount = d.dailyRate()
                .multiply(BigDecimal.valueOf(d.overdueDays()))
                .multiply(d.tierMultiplier())
                .setScale(2, RoundingMode.HALF_UP);
        String label = String.format("Base fine (%d day(s) x %s x %s)", d.overdueDays(), d.dailyRate(), d.tierMultiplier());
        return new FineBreakdown(amount, List.of(new LineItem(label, amount)));
    }
}
