package com.shikavani.lld.librarymanagement.enums;

import java.math.BigDecimal;

public enum MembershipTier {
    BASIC(BigDecimal.TWO),
    PREMIUM(BigDecimal.ONE),
    STUDENT(BigDecimal.valueOf(1.5));

    private final BigDecimal tierMultiplier;
    MembershipTier(BigDecimal multiplier){
        this.tierMultiplier = multiplier;
    }
    public BigDecimal getTierMultiplier() {
        return tierMultiplier;
    }
}
