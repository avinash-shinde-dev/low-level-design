package com.shikavani.lld.librarymanagement.service;

import com.shikavani.lld.librarymanagement.enums.MembershipStatus;
import com.shikavani.lld.librarymanagement.exception.MemberNotFoundException;
import com.shikavani.lld.librarymanagement.models.Member;
import com.shikavani.lld.librarymanagement.models.Membership;
import com.shikavani.lld.librarymanagement.registry.LockRegistry;
import com.shikavani.lld.librarymanagement.repository.MemberRepository;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.concurrent.locks.Lock;

/**
 * Member administration. (Role checks are intentionally left out for now: in a real system only
 * librarians would be allowed to call register / suspend / reactivate / changeMembership.)
 */
public final class MemberService {
    private final MemberRepository memberRepository;
    private final LockRegistry lock = LockRegistry.getInstance();

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public void register(Member member) {
        Objects.requireNonNull(member, "Member must not be null");
        if (memberRepository.findById(member.getId()).isPresent()) {
            throw new IllegalArgumentException("Member " + member.getId() + " is already registered");
        }
        memberRepository.save(member);
    }

    public Member getMemberById(String memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException("Member not found: " + memberId));
    }

    public void suspendMember(String memberId) {
        getMemberById(memberId).setStatus(MembershipStatus.SUSPENDED);
    }

    public void reactivate(String memberId) {
        getMemberById(memberId).setStatus(MembershipStatus.ACTIVE);
    }

    /** Upgrade or downgrade. Books already borrowed stay borrowed; the new limits apply to the NEXT borrow. */
    public void changeMembership(String memberId, Membership membership) {
        getMemberById(memberId).setMembership(membership);
    }

    /** Only records that the member paid (no payment processing). */
    public void recordFinePayment(String memberId, BigDecimal amount) {
        Lock memberLock = lock.member(memberId);
        memberLock.lock();
        try {
            getMemberById(memberId).recordFinePayment(amount);
        } finally {
            memberLock.unlock();
        }
    }
}
