package com.shikavani.lld.librarymanagement.models;

import com.shikavani.lld.librarymanagement.enums.MembershipTier;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * The rules of one membership tier. Nothing is hard-coded: create it with whatever numbers you want.
 *
 * @param maximumBorrows   how many books can be borrowed at the same time
 * @param loanDurationDays days until a borrowed book is due
 * @param fineMultiplier   fine = days x dailyRate x this (e.g. 0.5 = half price)
 * @param unpaidFineLimit  borrowing is blocked when unpaid fines are ABOVE this amount
 * @param pickupWindowHours how long a reserved copy waits for this member
 */
public record Membership(MembershipTier tier, int maximumBorrows, int loanDurationDays,
                         BigDecimal fineMultiplier, BigDecimal unpaidFineLimit, long pickupWindowHours) {
    public Membership {
        Objects.requireNonNull(tier, "tier");
        Objects.requireNonNull(fineMultiplier, "fineMultiplier");
        Objects.requireNonNull(unpaidFineLimit, "unpaidFineLimit");
        if (maximumBorrows <= 0 || loanDurationDays <= 0 || pickupWindowHours <= 0)
            throw new IllegalArgumentException("Borrow limit, loan days and pickup window must be positive");
        if (fineMultiplier.signum() < 0 || unpaidFineLimit.signum() < 0)
            throw new IllegalArgumentException("Fine multiplier and limit must not be negative");
    }
}
