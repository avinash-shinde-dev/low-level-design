package com.shikavani.lld.librarymanagement.service;

import com.shikavani.lld.librarymanagement.decorator.FineDecorator;
import com.shikavani.lld.librarymanagement.models.Book;
import com.shikavani.lld.librarymanagement.models.Member;
import com.shikavani.lld.librarymanagement.models.Transaction;
import com.shikavani.lld.librarymanagement.models.FineBreakdown;
import com.shikavani.lld.librarymanagement.models.FineDetails;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;

/** Collects the facts about a loan and hands them to the stack of fine rules. */
public class FineCalculationService {
    private final MemberService memberService;
    private final CatalogService catalogService;
    private final FineDecorator rules;
    private final BigDecimal dailyRate;      // e.g. 10 = Rs 10 per overdue day

    public FineCalculationService(MemberService memberService, CatalogService catalogService,
                                  FineDecorator rules, BigDecimal dailyRate) {
        this.memberService = memberService;
        this.catalogService = catalogService;
        this.rules = rules;
        this.dailyRate = dailyRate;
    }

    /** The fine if the loan were closed at 'asOf'. Only full overdue days count; never negative. */
    public FineBreakdown calculate(Transaction transaction, LocalDateTime asOf) {
        Member member = memberService.getMemberById(transaction.memberId());
        Book book = catalogService.getBook(transaction.bookId());
        long overdueDays = Math.max(0, Duration.between(transaction.dueAt(), asOf).toDays());
        return rules.calculateFine(new FineDetails(
                overdueDays, dailyRate, member.getMembership().fineMultiplier(), book.getPrice()));
    }
}
