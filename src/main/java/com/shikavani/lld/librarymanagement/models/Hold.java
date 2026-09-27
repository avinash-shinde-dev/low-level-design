package com.shikavani.lld.librarymanagement.models;

import com.shikavani.lld.librarymanagement.enums.HoldStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public class Hold {
    private final String holdId;
    private final String memberId;
    private final String bookId;
    private String bookCopyId;
    private HoldStatus status;
    private final LocalDateTime createdAt;
    private LocalDateTime pickupDeadline;

    public Hold(String memberId, String bookId, LocalDateTime createdAt) {
        this.holdId = UUID.randomUUID().toString();
        this.memberId = memberId;
        this.bookId = bookId;
        this.status = HoldStatus.WAITING;
        this.createdAt = createdAt;
    }

    public String getHoldId() {
        return holdId;
    }

    public String getMemberId() {
        return memberId;
    }

    public String getBookId() {
        return bookId;
    }

    public HoldStatus getStatus() {
        return status;
    }

    public void setStatus(HoldStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public String getBookCopyId() {
        return bookCopyId;
    }

    public void setBookCopyId(String bookCopyId) {
        this.bookCopyId = bookCopyId;
    }

    public LocalDateTime getPickupDeadline() {
        return pickupDeadline;
    }

    public void setPickupDeadline(LocalDateTime pickupDeadline) {
        this.pickupDeadline = pickupDeadline;
    }
}
