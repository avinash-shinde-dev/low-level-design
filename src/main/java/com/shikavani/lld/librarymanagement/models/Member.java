package com.shikavani.lld.librarymanagement.models;

import com.shikavani.lld.librarymanagement.enums.MembershipStatus;
import com.shikavani.lld.librarymanagement.models.membership.Membership;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public final class Member {
    private final String id;
    private final String name;
    private MembershipStatus status;
    private Membership membership;
    private Fine unpaidFines;
    private AtomicInteger currentBorrows;

    public Member(String id, String name, Membership membership) {
        this.id = id;
        this.name = name;
        this.membership = membership;
        this.status = MembershipStatus.ACTIVE;
        this.unpaidFines = new Fine(BigDecimal.ZERO, Currency.getInstance("INR"));
        this.currentBorrows = new AtomicInteger(0);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public MembershipStatus getStatus() {
        return status;
    }

    public void setStatus(MembershipStatus status) {
        this.status = status;
    }

    public Membership getMembership() {
        return membership;
    }

    public void setMembership(Membership membership) {
        Objects.requireNonNull(membership, "Member should have the membership");
        this.membership = membership;
    }

    public Fine getUnpaidFines() {
        return unpaidFines;
    }

    public void setUnpaidFines(Fine unpaidFines) {
        Objects.requireNonNull(unpaidFines, "Unpaid fine must not be null");
        this.unpaidFines = unpaidFines;
    }

    public boolean isSuspended(){
        return MembershipStatus.SUSPENDED.equals(this.status);
    }

    public boolean isActive(){
        return MembershipStatus.ACTIVE.equals(this.status);
    }

    public AtomicInteger getCurrentBorrows() {
        return currentBorrows;
    }

    public Integer incrementBorrows() {
        return this.currentBorrows.incrementAndGet();
    }
}
