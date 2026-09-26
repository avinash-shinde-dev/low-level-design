package com.shikavani.lld.librarymanagement.models.membership;

import com.shikavani.lld.librarymanagement.enums.MembershipTier;
import com.shikavani.lld.librarymanagement.models.Fine;

public final class PremiumMembership extends Membership{

    public PremiumMembership(Integer maximumBorrows, Integer loanDurationDays, Fine unpaidFineThreshold, Long pickupWindowHours) {
        super(maximumBorrows, loanDurationDays, unpaidFineThreshold, pickupWindowHours);
    }

    @Override
    public MembershipTier tier() {
        return MembershipTier.PREMIUM;
    }

}
