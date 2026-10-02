package com.shikavani.lld.librarymanagement.service;

import com.shikavani.lld.librarymanagement.enums.HoldStatus;
import com.shikavani.lld.librarymanagement.exception.HoldNotFoundException;
import com.shikavani.lld.librarymanagement.models.BookCopy;
import com.shikavani.lld.librarymanagement.models.Hold;
import com.shikavani.lld.librarymanagement.models.Member;
import com.shikavani.lld.librarymanagement.notification.EventPublisher;
import com.shikavani.lld.librarymanagement.notification.LibraryEvent;
import com.shikavani.lld.librarymanagement.registry.LockRegistry;
import com.shikavani.lld.librarymanagement.repository.HoldRepository;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;

public class HoldService {
    private final HoldRepository holdRepository;
    private final MemberService memberService;
    private final BranchService branchService;
    private final Map<String, Queue<Hold>> reservationQueue = new ConcurrentHashMap<>();
    private final Queue<BookCopy> bookCopyQueue = new ArrayDeque<>();
    private final LockRegistry lock = LockRegistry.getInstance();
    private final EventPublisher bus;
    public HoldService(HoldRepository holdRepository, MemberService memberService, BranchService branchService, EventPublisher bus) {
        this.holdRepository = holdRepository;
        this.memberService = memberService;
        this.branchService = branchService;
        this.bus = bus;
    }

    // This is the shared queue by multiple threads and the borrow service is already using locks
    public void placeHold(String memberId, String bookId){
        // check if the hold is already present in the repository for the same bookId and memberId
        if( isMemberAlreadyPlaceHold(memberId, bookId)){
            throw new RuntimeException("Cannot place hold as member %s has already place hold");
        }
        Hold hold = new Hold(memberId, bookId, LocalDateTime.now());
        reservationQueue.computeIfAbsent(bookId, k -> new ArrayDeque<>()).add(hold);
        this.holdRepository.save(hold);
    }

    // The cancel
    public boolean cancelHold(String holdId, String memberId){
        // cancel hold will
        Hold hold = holdRepository.findById(holdId).orElseThrow(() -> new HoldNotFoundException(String.format("Hold not found with id: %s", holdId)));

        if(!memberId.equals(hold.getMemberId())){
            throw new RuntimeException("Member doesn't own this hold");
        }
        Lock lockOnTitle = lock.title(hold.getBookId());
        lockOnTitle.lock();
        try {
            Queue queue = reservationQueue.get(hold.getBookId());
            if(queue != null){
                queue.remove(hold);
            }
            this.holdRepository.delete(holdId);

            // This means that this copy is on_hold to pick, we can assign the next Member
            if(hold.getStatus() == HoldStatus.READY_FOR_PICKUP){
                BookCopy copy  = this.branchService.getBookCopy(hold.getBookCopyId());

                this.fulfillNextHold(copy).ifPresentOrElse(next -> {
                    bus.publish(new LibraryEvent.HoldReady(this.memberService.getMemberById(next.getMemberId()), next));
                }, () -> copy.markAvailable());
                branchService.saveCopy(copy);
            }

            return true;
        }finally {
            lockOnTitle.unlock();
        }
    }

    // the handoff; caller already holds the title lock
    void offerToQueueIfAny(BookCopy copy){
        bookCopyQueue.offer(copy);
    }

    void markFulfilled(String holdId){

    }

    public Optional<Hold> fulfillNextHold(BookCopy copy) {
        final String bookId = copy.getBook().getId();

        Queue<Hold> queue = reservationQueue.get(bookId);

        if(queue == null) return Optional.empty();

        Hold next = queue.poll();

        if(next == null) return Optional.empty();

        Member member = memberService.getMemberById(next.getMemberId());

        next.setStatus(HoldStatus.READY_FOR_PICKUP);
        next.setBookCopyId(copy.getBookCopyId());
        next.setPickupDeadline(LocalDateTime.now().plusHours(member.getMembership().getPickupWindowHours()));
        copy.markOnHold();

        holdRepository.save(next);

        return Optional.of(next);
    }

    private boolean isMemberAlreadyPlaceHold(String memberId, String bookId) {
        Optional<Hold> existingHold = this.holdRepository.findAll()
                .stream()
                .filter(hold -> memberId.equals(hold.getMemberId()) && bookId.equals(hold.getBookId()))
                .findAny();

        return existingHold.isPresent();
    }

    public void triggerNotificationForExpiredHolds(){
         this.holdRepository.findAll()
                 .stream()
                 .filter(hold -> hold.getPickupDeadline().isBefore(LocalDateTime.now()))
                 .forEach(hold -> {
                     bus.publish(new LibraryEvent.HoldExpired(this.memberService.getMemberById(hold.getMemberId()), hold));
                 });
    }

}
