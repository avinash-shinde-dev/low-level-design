package com.shikavani.lld.librarymanagement.decorator;

import com.shikavani.lld.librarymanagement.models.fine.FineBreakdown;
import com.shikavani.lld.librarymanagement.models.fine.FineDetails;

public class CapDecorator implements FineDecorator{
    private final FineDecorator fineDecorator;

    public CapDecorator(FineDecorator fineDecorator) {
        this.fineDecorator = fineDecorator;
    }

    @Override
    public FineBreakdown calculateFine(FineDetails details) {
        FineBreakdown breakdown = this.fineDecorator.calculateFine(details);

         if(breakdown.total().compareTo(details.bookPrice()) > 0 ){
             return breakdown.withTotal(details.bookPrice());
         }

        return breakdown;
    }
}
