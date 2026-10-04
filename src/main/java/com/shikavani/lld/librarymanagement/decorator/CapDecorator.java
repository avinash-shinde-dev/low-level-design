package com.shikavani.lld.librarymanagement.decorator;

import com.shikavani.lld.librarymanagement.models.fine.FineBreakdown;
import com.shikavani.lld.librarymanagement.models.fine.FineDetails;

/** The total fine never goes above the price of the book. Put it OUTERMOST so it caps everything. */
public class CapDecorator implements FineDecorator {
    private final FineDecorator inner;

    public CapDecorator(FineDecorator inner) { this.inner = inner; }

    @Override
    public FineBreakdown calculateFine(FineDetails details) {
        FineBreakdown breakdown = inner.calculateFine(details);
        if (breakdown.total().compareTo(details.bookPrice()) > 0) {
            // add a negative line so the breakdown still adds up and shows why it was reduced
            return breakdown.plus("Cap adjustment (book price)", details.bookPrice().subtract(breakdown.total()));
        }
        return breakdown;
    }
}
