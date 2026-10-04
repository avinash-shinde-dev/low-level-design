package com.shikavani.lld.librarymanagement.service;

import com.shikavani.lld.librarymanagement.enums.BookCopyStatus;
import com.shikavani.lld.librarymanagement.enums.HoldStatus;
import com.shikavani.lld.librarymanagement.exception.BookCopyNotAvailableException;
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

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;

public class HoldService {
    private final HoldRepository holdRepository;
    private final MemberService memberService;
    private final BookCopyRepository bookCopyRepository;
    private final Map<String, Queue<Hold>> reservationQueue = new ConcurrentHashMap<>();
    private final Queue<BookCopy> bookCopyQueue = new ArrayDeque<>();
    private final LockRegistry lock = LockRegistry.getInstance();
    private final EventPublisher bus;

    public HoldService(HoldRepository holdRepository, MemberService memberService, BookCopyRepository bookCopyRepository, EventPublisher bus) {
        this.holdRepository = holdRepository;
        this.memberService = memberService;
        this.bookCopyRepository = bookCopyRepository;
        this.bus = bus;
    }

    // This is the shared queue by multiple threads and the borrow service is already using locks
    public void placeHold(String memberId, String bookId) {
        Objects.requireNonNull(memberId, "Member id must not be null");
        Objects.requireNonNull(bookId, "Book id must not be null");

        // check if the hold is already present in the repository for the same bookId and memberId
        if (hasActiveHold(memberId, bookId)) {
            throw new HoldException("Cannot place hold as member %s has already place hold");
        }
        if (hasAvailableCopy(bookId)) {
            throw new HoldException("A copy of this title is available; borrow it instead of placing a hold");
        }

        Hold hold = new Hold(memberId, bookId, LocalDateTime.now());
        reservationQueue.computeIfAbsent(bookId, k -> new ArrayDeque<>()).add(hold);
        this.holdRepository.save(hold);
    }

    // The cancel
    public boolean cancelHold(String holdId, String memberId) {
        Objects.requireNonNull(holdId, "Hold Id must not be null");
        Objects.requireNonNull(memberId, "Member Id must not be null");
        this.memberService.getMemberById(memberId); // fail fast if the member is not available

        // cancel hold will
        Hold hold = holdRepository.findById(holdId).orElseThrow(() -> new HoldNotFoundException(String.format("Hold not found with id: %s", holdId)));

        if (!memberId.equals(hold.getMemberId())) {
            throw new RuntimeException("Member doesn't own this hold");
        }
        Lock lockOnTitle = lock.title(hold.getBookId());
        lockOnTitle.lock();
        try {
            Queue queue = reservationQueue.get(hold.getBookId());
            if (queue != null) {
                queue.remove(hold);
            }
            this.holdRepository.delete(holdId);

            // This means that this copy is on_hold to pick, we can assign the next Member
            if (hold.getStatus() == HoldStatus.READY_FOR_PICKUP) {
                BookCopy copy = this.bookCopyRepository.findById(hold.getBookCopyId()).orElseThrow(() -> new BookCopyNotAvailableException(String.format("Book copy : %s not available", hold.getBookCopyId())));
                if(this.fulfillNextHold(copy).isEmpty()){
                    copy.markAvailable();
                }
                this.bookCopyRepository.save(copy);
            }
            return true;
        } finally {
            lockOnTitle.unlock();
        }
    }

    // the handoff; caller already holds the title lock
    public void offerToQueueIfAny(BookCopy copy) {
        bookCopyQueue.offer(copy);
    }

    public void markFulfilled(String holdId){
        Hold hold = this.holdRepository.findById(holdId)
                .orElseThrow(() -> new HoldNotFoundException(String.format("Hold not found with id: %s", holdId)));
        Lock lockOnTitle = lock.title(hold.getBookId());
        lockOnTitle.lock();
        try{
            hold.setStatus(HoldStatus.FULFILLED);
            this.holdRepository.save(hold);
        }finally {
            lockOnTitle.unlock();
        }
    }

    public Optional<Hold> fulfillNextHold(BookCopy copy) {
        Objects.requireNonNull(copy, "Book copy must not be null");

        final String bookId = copy.getBook().getId();

        Lock lockOnTitle = lock.title(bookId);
        lockOnTitle.lock();
        try {
            Queue<Hold> queue = reservationQueue.get(bookId);

            if (queue == null) return Optional.empty();

            Hold next = queue.poll();

            if (next == null) return Optional.empty();

            Member member = memberService.getMemberById(next.getMemberId());

            next.setStatus(HoldStatus.READY_FOR_PICKUP);
            next.setBookCopyId(copy.getBookCopyId());
            next.setPickupDeadline(LocalDateTime.now().plusHours(member.getMembership().getPickupWindowHours()));
            copy.markOnHold();
            holdRepository.save(next);
            // publish the event holdReady
            bus.publish(new LibraryEvent.HoldReady(member, next));
            return Optional.of(next);
        } finally {
            lockOnTitle.unlock();
        }
    }

    public List<Hold> getWaitingHolds(String bookId) {
        Lock lockOnTitle = lock.title(bookId);
        lockOnTitle.lock();
        try{
            Queue<Hold> queue = this.reservationQueue.get(bookId);

            return queue == null ? List.of() : List.copyOf(queue);
        }finally {
            lockOnTitle.unlock();
        }
    }

    public Optional<Hold> findReadyHold(String memberId, String bookId){
        Lock lockOnTitle = lock.title(bookId);
        lockOnTitle.lock();
        try {
            return this.holdRepository.findAll()
                    .stream()
                    .filter(hold -> memberId.equals(hold.getMemberId()) &&
                            bookId.equals(hold.getBookId()) &&
                            hold.getStatus() == HoldStatus.READY_FOR_PICKUP)
                    .findFirst();
        }finally {
            lockOnTitle.unlock();
        }
    }
    private boolean hasActiveHold(String memberId, String bookId) {

        return this.holdRepository.findAll()
                .stream()
                .anyMatch(hold -> memberId.equals(hold.getMemberId())
                        && bookId.equals(hold.getBookId())
                        && List.of(HoldStatus.READY_FOR_PICKUP, HoldStatus.WAITING).contains(hold.getStatus()));

    }

    private boolean hasAvailableCopy(String bookId) {
        return this.bookCopyRepository.findAll().stream()
                .anyMatch(c -> bookId.equals(c.getBook().getId()) && c.getStatus() == BookCopyStatus.AVAILABLE);
    }

    public void triggerNotificationForExpiredHolds() {
        this.holdRepository.findAll()
                .stream()
                .filter(hold -> hold.getPickupDeadline().isBefore(LocalDateTime.now()))
                .forEach(hold -> {
                    bus.publish(new LibraryEvent.HoldExpired(this.memberService.getMemberById(hold.getMemberId()), hold));
                });
    }

}
