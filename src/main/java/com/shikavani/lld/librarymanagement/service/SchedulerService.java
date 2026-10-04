package com.shikavani.lld.librarymanagement.service;

import com.shikavani.lld.librarymanagement.models.Member;
import com.shikavani.lld.librarymanagement.models.Transaction;
import com.shikavani.lld.librarymanagement.models.FineBreakdown;
import com.shikavani.lld.librarymanagement.notification.EventPublisher;
import com.shikavani.lld.librarymanagement.notification.LibraryEvent;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Operations a scheduler (cron / timer) would call, e.g. every hour. Nothing runs by itself here.
 * Every method is safe to call again and again: nobody gets the same notice twice.
 */
public class SchedulerService {
    private final TransactionService transactionService;
    private final MemberService memberService;
    private final FineCalculationService fineCalculationService;
    private final HoldService holdService;
    private final EventPublisher bus;
    private final Clock clock;
    private final Duration reminderWindow;      // e.g. 2 days = remind when the due date is within 2 days

    private final Set<String> remindedLoans = ConcurrentHashMap.newKeySet();                 // loans already reminded
    private final Map<String, LocalDate> lastOverdueNotice = new ConcurrentHashMap<>();      // loan -> day of last notice

    public SchedulerService(TransactionService transactionService, MemberService memberService,
                            FineCalculationService fineCalculationService, HoldService holdService,
                            EventPublisher bus, Clock clock, Duration reminderWindow) {
        this.transactionService = transactionService;
        this.memberService = memberService;
        this.fineCalculationService = fineCalculationService;
        this.holdService = holdService;
        this.bus = bus;
        this.clock = clock;
        this.reminderWindow = reminderWindow;
    }

    /** One reminder per loan, sent once the due date is within the reminder window. Returns how many were sent. */
    public int sendDueDateReminders() {
        LocalDateTime now = LocalDateTime.now(clock);
        int sent = 0;
        for (Transaction t : transactionService.findActiveLoans()) {
            boolean dueSoon = t.dueAt().isAfter(now) && !t.dueAt().isAfter(now.plus(reminderWindow));
            if (dueSoon && remindedLoans.add(t.id())) {            // add() is true only for the first caller
                bus.publish(new LibraryEvent.DueSoon(memberService.getMemberById(t.memberId()), t));
                sent++;
            }
        }
        return sent;
    }

    /** One overdue-and-fine notice per loan per day. Returns how many were sent. */
    public int sendOverdueNotices() {
        LocalDateTime now = LocalDateTime.now(clock);
        int sent = 0;
        for (Transaction t : transactionService.findOverdueLoans()) {
            LocalDate today = now.toLocalDate();
            if (!today.equals(lastOverdueNotice.put(t.id(), today))) {
                Member member = memberService.getMemberById(t.memberId());
                FineBreakdown fineSoFar = fineCalculationService.calculate(t, now);
                bus.publish(new LibraryEvent.OverDue(member, t, fineSoFar));
                sent++;
            }
        }
        return sent;
    }

    /** Expires reserved copies that were not collected in time. Returns how many holds expired. */
    public int expireHolds() {
        return holdService.expireOverdueHolds();
    }

    public void runAll() {
        sendDueDateReminders();
        sendOverdueNotices();
        expireHolds();
    }
}
