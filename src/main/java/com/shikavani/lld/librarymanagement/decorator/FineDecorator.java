package com.shikavani.lld.librarymanagement.decorator;

import com.shikavani.lld.librarymanagement.models.fine.FineBreakdown;
import com.shikavani.lld.librarymanagement.models.fine.FineDetails;

public interface FineDecorator {

    FineBreakdown calculateFine(FineDetails details);
}
