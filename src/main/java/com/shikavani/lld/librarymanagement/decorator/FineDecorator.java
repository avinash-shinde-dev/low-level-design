package com.shikavani.lld.librarymanagement.decorator;

import com.shikavani.lld.librarymanagement.models.FineBreakdown;
import com.shikavani.lld.librarymanagement.models.FineDetails;

/**
 * One fine rule. Rules are stacked like Russian dolls, each wrapping the one inside it, e.g.
 *     new CapDecorator(new LongTermPenaltyDecorator(new GracePeriodDecorator(new BaseFineDecorator(), 2), 30, 50))
 * To add a new rule (weekend-free, holiday-free...) write ONE new class and add it to the stack.
 * No existing class changes.
 */
public interface FineDecorator {
    FineBreakdown calculateFine(FineDetails details);
}
