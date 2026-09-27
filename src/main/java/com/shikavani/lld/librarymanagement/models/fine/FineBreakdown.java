package com.shikavani.lld.librarymanagement.models.fine;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public record FineBreakdown(BigDecimal total, List<LineItem> lineItems) {

    public FineBreakdown plus(String label, BigDecimal amount){
        List<LineItem> lineItems = new ArrayList<>(lineItems());
        lineItems.add(new LineItem(label, amount));
        return new FineBreakdown(total.add(amount), lineItems);
    }
    public FineBreakdown withTotal(BigDecimal total){
        return new FineBreakdown(total, lineItems);
    }
}
