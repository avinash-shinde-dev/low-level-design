package com.shikavani.lld.librarymanagement.service;

import com.shikavani.lld.librarymanagement.decorator.FineDecorator;
import com.shikavani.lld.librarymanagement.models.Book;
import com.shikavani.lld.librarymanagement.models.Member;
import com.shikavani.lld.librarymanagement.models.Transaction;
import com.shikavani.lld.librarymanagement.models.fine.FineBreakdown;
import com.shikavani.lld.librarymanagement.models.fine.FineDetails;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;

public class FineCalculationService {

    private final MemberService memberService;
    private final CatalogService catalogService;
    private final FineDecorator chain;
    private final BigDecimal dailyRate;

    public FineCalculationService(MemberService memberService, CatalogService catalogService, FineDecorator chain, BigDecimal dailyRate) {
        this.memberService = memberService;
        this.catalogService = catalogService;
        this.chain = chain;
        this.dailyRate = dailyRate;
    }

    public FineBreakdown calculate(Transaction transaction){

        Member member = this.memberService.getMemberById(transaction.memberId());

        Book book = this.catalogService.searchById(transaction.bookId());

        long overdueDays = Duration.between(transaction.dueAt(), LocalDateTime.now()).toDays();

        FineDetails fineDetails = new FineDetails(
                overdueDays,
                BigDecimal.ONE.add(dailyRate), // daily rate 20%
                member.getMembership().tier().getTierMultiplier(),
                book.getPrice());

        return chain.calculateFine(fineDetails);
    }
}
