package com.shikavani.lld.librarymanagement.models.membership;

import com.shikavani.lld.librarymanagement.enums.MembershipTier;
import com.shikavani.lld.librarymanagement.models.Fine;

import java.util.Objects;

public abstract class Membership {
    private final Integer maximumBorrows;
    private final Integer loanDurationDays;
    private Fine unpaidFineThreshold;
    private final Long pickupWindowHours;
    protected Membership(Integer maximumBorrows, Integer loanDurationDays, Fine unpaidFineThreshold, Long pickupWindowHours) {
        this.maximumBorrows = maximumBorrows;
        this.loanDurationDays = loanDurationDays;
        this.unpaidFineThreshold = unpaidFineThreshold;
        this.pickupWindowHours = pickupWindowHours;
    }

    public abstract  MembershipTier tier();

    public Integer getMaximumBorrows() {
        return maximumBorrows;
    }

    public Integer getLoanDurationDays() {
        return loanDurationDays;
    }

    public Long getPickupWindowHours() {
        return pickupWindowHours;
    }

    public Fine getUnpaidFineThreshold(){
        return this.unpaidFineThreshold;
    }

    public void setUnpaidFineThreshold(Fine fine){
        Objects.requireNonNull(fine, "Fine must not be null");
        this.unpaidFineThreshold = fine;
    }
}
