package com.shikavani.lld.librarymanagement.models;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/** The final fine plus every line that built it up (base fine, penalties, cap...). Stored on the Transaction. */
public record FineBreakdown(BigDecimal total, List<LineItem> lineItems) {

    /** Returns a new breakdown with one more line; the amount can be negative (e.g. a cap adjustment). */
    public FineBreakdown plus(String label, BigDecimal amount) {
        List<LineItem> items = new ArrayList<>(lineItems);
        items.add(new LineItem(label, amount));
        return new FineBreakdown(total.add(amount), List.copyOf(items));
    }

    @Override public String toString() { return total + " " + lineItems; }
}
