package com.shikavani.lld.librarymanagement.service;

import com.shikavani.lld.librarymanagement.enums.BookCopyStatus;
import com.shikavani.lld.librarymanagement.enums.HoldStatus;
import com.shikavani.lld.librarymanagement.exception.BookCopyNotFoundException;
import com.shikavani.lld.librarymanagement.exception.HoldException;
import com.shikavani.lld.librarymanagement.exception.HoldNotFoundException;
import com.shikavani.lld.librarymanagement.models.BookCopy;
import com.shikavani.lld.librarymanagement.models.Hold;
import com.shikavani.lld.librarymanagement.models.Member;
import com.shikavani.lld.librarymanagement.notification.EventPublisher;
import com.shikavani.lld.librarymanagement.notification.LibraryEvent;
import com.shikavani.lld.librarymanagement.registry.LockRegistry;
import com.shikavani.lld.librarymanagement.repository.BookCopyRepository;
import com.shikavani.lld.librarymanagement.repository.HoldRepository;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;

/**
 * Hold (reservation) queue.
 *
 * Decision: a hold covers the WHOLE NETWORK for a title (not one branch). The first copy that becomes free
 * anywhere goes to the head of the queue, and the member collects it at the branch where it is waiting.
 *
 * Safety: every queue change happens while holding that BOOK's lock, so FIFO order is never broken
 * and nothing is lost or duplicated when threads place / cancel / expire holds at the same time.
 */
public class HoldService {
    private final HoldRepository holdRepository;
    private final MemberService memberService;
    private final BookCopyRepository bookCopyRepository;
    private final EventPublisher bus;
    private final Clock clock;
    private final LockRegistry lock = LockRegistry.getInstance();

    // bookId -> members waiting, oldest first. Only touched while holding that book's lock.
    private final Map<String, Queue<Hold>> waitingQueues = new ConcurrentHashMap<>();

    public HoldService(HoldRepository holdRepository, MemberService memberService,
                       BookCopyRepository bookCopyRepository, EventPublisher bus, Clock clock) {
        this.holdRepository = holdRepository;
        this.memberService = memberService;
        this.bookCopyRepository = bookCopyRepository;
        this.bus = bus;
        this.clock = clock;
    }

    /** Joins the end of the queue. Not allowed twice for the same title, or while a copy is available. */
    public Hold placeHold(String memberId, String bookId) {
        Objects.requireNonNull(memberId, "Member id must not be null");
        Objects.requireNonNull(bookId, "Book id must not be null");
        memberService.getMemberById(memberId);                 // fails if the member does not exist

        Lock bookLock = lock.book(bookId);
        bookLock.lock();
        try {
            if (hasActiveHold(memberId, bookId)) {
                throw new HoldException("Member " + memberId + " already has an active hold on this book");
            }
            if (hasAvailableCopy(bookId)) {
                throw new HoldException("A copy is available; borrow it instead of placing a hold");
            }
            Hold hold = new Hold(memberId, bookId, LocalDateTime.now(clock));
            waitingQueues.computeIfAbsent(bookId, k -> new ArrayDeque<>()).add(hold);
            holdRepository.save(hold);
            return hold;
        } finally {
            bookLock.unlock();
        }
    }

    /**
     * Member cancels. A WAITING hold just leaves the queue. A READY hold gives its reserved copy
     * to the next member in the queue (or back to the shelf if nobody is waiting).
     */
    public void cancelHold(String holdId, String memberId) {
        String bookId = findHold(holdId).getBookId();          // a hold's book never changes, so reading it early is safe
        Lock bookLock = lock.book(bookId);
        bookLock.lock();
        try {
            Hold hold = findHold(holdId);                      // read the status again, now that we hold the lock
            if (!hold.getMemberId().equals(memberId)) {
                throw new HoldException("This hold belongs to another member");
            }
            switch (hold.getStatus()) {
                case WAITING -> {
                    waitingQueues.get(bookId).remove(hold);
                    hold.setStatus(HoldStatus.CANCELLED);
                    holdRepository.save(hold);
                }
                case READY_FOR_PICKUP -> {
                    hold.setStatus(HoldStatus.CANCELLED);
                    holdRepository.save(hold);
                    passReservedCopyOn(hold);
                }
                default -> throw new HoldException("Hold is already " + hold.getStatus());
            }
        } finally {
            bookLock.unlock();
        }
    }

    /** A copy just became free: give it to the first member in the queue. Empty = nobody was waiting. */
    public Optional<Hold> fulfillNextHold(BookCopy copy) {
        String bookId = copy.getBook().getId();
        Lock bookLock = lock.book(bookId);
        bookLock.lock();
        try {
            Queue<Hold> queue = waitingQueues.get(bookId);
            Hold next = queue == null ? null : queue.poll();
            if (next == null) return Optional.empty();

            Member member = memberService.getMemberById(next.getMemberId());
            next.setStatus(HoldStatus.READY_FOR_PICKUP);
            next.setBookCopyId(copy.getBookCopyId());
            next.setPickupDeadline(LocalDateTime.now(clock).plusHours(member.getMembership().pickupWindowHours()));
            if (copy.getStatus() != BookCopyStatus.ON_HOLD) {      // (already ON_HOLD when passed on from another hold)
                copy.changeStatus(BookCopyStatus.ON_HOLD);
            }
            holdRepository.save(next);
            bus.publish(new LibraryEvent.HoldReady(member, next));
            return Optional.of(next);
        } finally {
            bookLock.unlock();
        }
    }

    /** The member borrowed the reserved copy. */
    public void markFulfilled(String holdId) {
        Hold hold = findHold(holdId);
        Lock bookLock = lock.book(hold.getBookId());
        bookLock.lock();
        try {
            hold.setStatus(HoldStatus.FULFILLED);
            holdRepository.save(hold);
        } finally {
            bookLock.unlock();
        }
    }

    /** The member's own reserved hold that can still be collected (not past its deadline). */
    public Optional<Hold> findReadyHold(String memberId, String bookId) {
        LocalDateTime now = LocalDateTime.now(clock);
        Lock bookLock = lock.book(bookId);
        bookLock.lock();
        try {
            return holdRepository.findAll().stream()
                    .filter(h -> h.getStatus() == HoldStatus.READY_FOR_PICKUP        // status first: only READY holds have a deadline
                            && h.getMemberId().equals(memberId)
                            && h.getBookId().equals(bookId)
                            && !h.getPickupDeadline().isBefore(now))
                    .findFirst();
        } finally {
            bookLock.unlock();
        }
    }

    /** The members waiting for a title, first in line first. */
    public List<Hold> getWaitingHolds(String bookId) {
        Lock bookLock = lock.book(bookId);
        bookLock.lock();
        try {
            Queue<Hold> queue = waitingQueues.get(bookId);
            return queue == null ? List.of() : List.copyOf(queue);
        } finally {
            bookLock.unlock();
        }
    }

    /** True if somebody is still waiting for, or holding a reserved copy of, this title. */
    public boolean hasActiveHolds(String bookId) {
        return holdRepository.findAll().stream()
                .anyMatch(h -> h.getBookId().equals(bookId) && isActive(h));
    }

    /**
     * SCHEDULER OPERATION: expires reserved copies nobody collected in time; each copy then moves to the
     * next member in the queue. Safe to run again and again. Returns how many holds expired.
     */
    public int expireOverdueHolds() {
        LocalDateTime now = LocalDateTime.now(clock);
        int expired = 0;
        for (Hold candidate : holdRepository.findAll()) {
            if (!isPastDeadline(candidate, now)) continue;
            Lock bookLock = lock.book(candidate.getBookId());
            bookLock.lock();
            try {
                if (!isPastDeadline(candidate, now)) continue;           // collected or cancelled while we waited for the lock
                candidate.setStatus(HoldStatus.EXPIRED);
                holdRepository.save(candidate);
                bus.publish(new LibraryEvent.HoldExpired(memberService.getMemberById(candidate.getMemberId()), candidate));
                passReservedCopyOn(candidate);
                expired++;
            } finally {
                bookLock.unlock();
            }
        }
        return expired;
    }

    // ---------------------------------------------------------------- helpers

    /** The hold ended (cancelled / expired): its copy goes to the next in the queue, else back on the shelf. Caller holds the book lock. */
    private void passReservedCopyOn(Hold ended) {
        Lock copyLock = lock.bookCopy(ended.getBookCopyId());
        copyLock.lock();
        try {
            BookCopy copy = bookCopyRepository.findById(ended.getBookCopyId())
                    .orElseThrow(() -> new BookCopyNotFoundException("Book copy not found: " + ended.getBookCopyId()));
            if (copy.getStatus() != BookCopyStatus.ON_HOLD) return;
            if (fulfillNextHold(copy).isEmpty()) {
                copy.changeStatus(BookCopyStatus.AVAILABLE);
            }
            bookCopyRepository.save(copy);
        } finally {
            copyLock.unlock();
        }
    }

    private static boolean isActive(Hold h) {
        return h.getStatus() == HoldStatus.WAITING || h.getStatus() == HoldStatus.READY_FOR_PICKUP;
    }

    private static boolean isPastDeadline(Hold h, LocalDateTime now) {
        return h.getStatus() == HoldStatus.READY_FOR_PICKUP && h.getPickupDeadline().isBefore(now);
    }

    private boolean hasActiveHold(String memberId, String bookId) {
        return holdRepository.findAll().stream()
                .anyMatch(h -> h.getMemberId().equals(memberId) && h.getBookId().equals(bookId) && isActive(h));
    }

    private boolean hasAvailableCopy(String bookId) {
        return bookCopyRepository.findAll().stream()
                .anyMatch(c -> c.getBook().getId().equals(bookId) && c.getStatus() == BookCopyStatus.AVAILABLE);
    }

    private Hold findHold(String holdId) {
        return holdRepository.findById(holdId)
                .orElseThrow(() -> new HoldNotFoundException("Hold not found: " + holdId));
    }
}
