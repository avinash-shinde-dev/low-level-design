package com.shikavani.lld.librarymanagement.models;

import java.time.LocalDateTime;

/**
 * One loan. returnTimeStamp == null means the book is still out.
 * Lifecycle: BORROWED (before dueAt) -> OVERDUE (after dueAt) -> RETURNED (returnTimeStamp set).
 * The status is worked out from the dates, so it can never get out of sync with them.
 */
public record Transaction(String id, String bookCopyId, String bookId, String branchId, String memberId,
                          LocalDateTime issuedAt, LocalDateTime dueAt,
                          LocalDateTime returnTimeStamp, FineBreakdown fineBreakdown) {

    public boolean isReturned() { return returnTimeStamp != null; }

    public boolean isOverdueAt(LocalDateTime now) { return !isReturned() && dueAt.isBefore(now); }

    public Transaction closeTransaction(FineBreakdown fine, LocalDateTime returnedAt) {
        return new Transaction(id, bookCopyId, bookId, branchId, memberId, issuedAt, dueAt, returnedAt, fine);
    }
}
