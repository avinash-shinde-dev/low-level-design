package com.shikavani.lld.librarymanagement.models;

import com.shikavani.lld.librarymanagement.enums.ChannelType;
import com.shikavani.lld.librarymanagement.enums.MembershipStatus;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public final class Member {
    private final String id;
    private final String name;
    private final List<ChannelType> preferredChannels;
    private final AtomicInteger currentBorrows = new AtomicInteger();
    private volatile MembershipStatus status = MembershipStatus.ACTIVE;
    private volatile Membership membership;
    private volatile BigDecimal unpaidFines = BigDecimal.ZERO;   // single currency, no floating point

    public Member(String id, String name, Membership membership, List<ChannelType> preferredChannels) {
        this.id = Objects.requireNonNull(id);
        this.name = Objects.requireNonNull(name);
        this.membership = Objects.requireNonNull(membership);
        this.preferredChannels = List.copyOf(preferredChannels);
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public MembershipStatus getStatus() { return status; }
    public void setStatus(MembershipStatus status) { this.status = Objects.requireNonNull(status); }
    public boolean isSuspended() { return status == MembershipStatus.SUSPENDED; }
    public Membership getMembership() { return membership; }
    public void setMembership(Membership membership) { this.membership = Objects.requireNonNull(membership); }
    public BigDecimal getUnpaidFines() { return unpaidFines; }
    public List<ChannelType> preferredNotificationChannels() { return preferredChannels; }

    // The three methods below change the borrow count: callers hold the member's lock (see LockRegistry).
    public int getCurrentBorrows() { return currentBorrows.get(); }
    public boolean hasFreeSlot() { return currentBorrows.get() < membership.maximumBorrows(); }
    public void incrementBorrows() { currentBorrows.incrementAndGet(); }
    public void decrementBorrows() { currentBorrows.updateAndGet(c -> Math.max(0, c - 1)); }

    public void addUnpaidFine(BigDecimal amount) {
        if (amount.signum() < 0) throw new IllegalArgumentException("Fine must not be negative");
        unpaidFines = unpaidFines.add(amount);
    }

    public void recordFinePayment(BigDecimal amount) {
        if (amount.signum() <= 0) throw new IllegalArgumentException("Payment must be positive");
        if (amount.compareTo(unpaidFines) > 0)
            throw new IllegalArgumentException("Payment " + amount + " is more than the unpaid fines " + unpaidFines);
        unpaidFines = unpaidFines.subtract(amount);
    }

    @Override public String toString() { return name; }
}
