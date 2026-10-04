package com.shikavani.lld.librarymanagement.models;

import com.shikavani.lld.librarymanagement.enums.HoldStatus;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/** A member's place in the queue for a title. Changed only while holding that book's lock. */
public class Hold {
    private final String holdId = UUID.randomUUID().toString();
    private final String memberId;
    private final String bookId;
    private final LocalDateTime createdAt;
    private String bookCopyId;               // set once a copy is kept aside for this hold
    private LocalDateTime pickupDeadline;    // set at the same time
    private HoldStatus status = HoldStatus.WAITING;

    public Hold(String memberId, String bookId, LocalDateTime createdAt) {
        this.memberId = Objects.requireNonNull(memberId);
        this.bookId = Objects.requireNonNull(bookId);
        this.createdAt = Objects.requireNonNull(createdAt);
    }

    public String getHoldId() { return holdId; }
    public String getMemberId() { return memberId; }
    public String getBookId() { return bookId; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public HoldStatus getStatus() { return status; }
    public void setStatus(HoldStatus status) { this.status = status; }
    public String getBookCopyId() { return bookCopyId; }
    public void setBookCopyId(String bookCopyId) { this.bookCopyId = bookCopyId; }
    public LocalDateTime getPickupDeadline() { return pickupDeadline; }
    public void setPickupDeadline(LocalDateTime pickupDeadline) { this.pickupDeadline = pickupDeadline; }
}
