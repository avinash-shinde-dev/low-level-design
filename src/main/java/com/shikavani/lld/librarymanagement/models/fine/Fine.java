package com.shikavani.lld.librarymanagement.models.fine;

import java.math.BigDecimal;
import java.util.Currency;

public record Fine(BigDecimal fine, Currency currency) implements Comparable<Fine>{
    @Override
    public int compareTo(Fine o) {
        return this.fine.compareTo(o.fine);
    }
}
