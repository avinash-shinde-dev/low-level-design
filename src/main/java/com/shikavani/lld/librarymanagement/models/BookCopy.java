package com.shikavani.lld.librarymanagement.models;

import com.shikavani.lld.librarymanagement.enums.BookCopyStatus;

import java.util.*;

import static com.shikavani.lld.librarymanagement.enums.BookCopyStatus.*;

/** One physical copy. It belongs to exactly one branch and has a status. */
public final class BookCopy {

    /** The ONLY allowed status changes. Anything else is rejected by changeStatus(). */
    private static final Map<BookCopyStatus, Set<BookCopyStatus>> ALLOWED = Map.of(
            AVAILABLE,  EnumSet.of(BORROWED, ON_HOLD, IN_TRANSIT, LOST, REMOVED),
            BORROWED,   EnumSet.of(AVAILABLE, LOST),
            ON_HOLD,    EnumSet.of(BORROWED, AVAILABLE),
            IN_TRANSIT, EnumSet.of(AVAILABLE),
            LOST,       EnumSet.of(REMOVED),
            REMOVED,    EnumSet.noneOf(BookCopyStatus.class));

    private final String bookCopyId = UUID.randomUUID().toString();
    private final Book book;
    private volatile String branchId;
    private volatile BookCopyStatus status = AVAILABLE;

    public BookCopy(Book book, String branchId) {
        this.book = Objects.requireNonNull(book, "Book must not be null");
        this.branchId = Objects.requireNonNull(branchId, "Branch id must not be null");
    }

    public String getBookCopyId() { return bookCopyId; }
    public Book getBook() { return book; }
    public String getBranchId() { return branchId; }
    public BookCopyStatus getStatus() { return status; }

    /** Callers hold the copy's lock (see LockRegistry), so check-then-set is safe. */
    public void changeStatus(BookCopyStatus next) {
        if (!ALLOWED.get(status).contains(next)) {
            throw new IllegalStateException("Invalid copy status change: " + status + " -> " + next);
        }
        this.status = next;
    }

    /** A copy returned at another branch simply becomes part of that branch's inventory. */
    public void moveToBranch(String newBranchId) {
        this.branchId = Objects.requireNonNull(newBranchId);
    }
}
