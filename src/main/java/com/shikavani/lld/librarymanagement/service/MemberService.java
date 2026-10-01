package com.shikavani.lld.librarymanagement.service;

import com.shikavani.lld.librarymanagement.enums.MembershipStatus;
import com.shikavani.lld.librarymanagement.exception.MemberNotFoundException;
import com.shikavani.lld.librarymanagement.models.Member;
import com.shikavani.lld.librarymanagement.models.membership.Membership;
import com.shikavani.lld.librarymanagement.repository.MemberRepository;

import java.util.NoSuchElementException;
import java.util.Objects;

public final class MemberService {
    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public void register(Member member){
        Objects.requireNonNull(member, "Member must not be null");
        this.memberRepository.save(member);
    }

    public Member getMemberById(String memberId){
        return memberRepository.findById(memberId).orElseThrow(() -> new MemberNotFoundException(String.format("Member: %s not found", memberId)));
    }

    public void suspendMember(String memberId){
        Objects.requireNonNull(memberId, "Member id must not be null");
        Member member = this.memberRepository.findById(memberId).orElseThrow(() -> new NoSuchElementException("Member not found exception"));
        member.setStatus(MembershipStatus.SUSPENDED);
        // persist it back
        this.memberRepository.save(member);
    }

    public void reactivate(String memberId){
        Objects.requireNonNull(memberId, "Member id must not be null");
        Member member = this.memberRepository.findById(memberId).orElseThrow(() -> new NoSuchElementException("Member not found exception"));
        if(member.getStatus().equals(MembershipStatus.ACTIVE)){
            System.out.printf("Member %s is already active", member.getName());
            return;
        }
        member.setStatus(MembershipStatus.ACTIVE);
        // persist it back
        this.memberRepository.save(member);
    }

    public void setMembership(String memberId, Membership membership){
        Objects.requireNonNull(memberId, "Member id must not be null");
        Member member = this.memberRepository.findById(memberId).orElseThrow(() -> new NoSuchElementException("Member not found exception"));
        if(member.getStatus().equals(MembershipStatus.SUSPENDED)){
            System.out.printf("Member : %s is suspended", member.getName());
        }
        member.setMembership(membership);
        this.memberRepository.save(member);
    }

}
