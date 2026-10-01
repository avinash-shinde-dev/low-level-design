package com.shikavani.lld.librarymanagement.repository;

import com.shikavani.lld.librarymanagement.models.Member;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class MemberRepository implements InMemoryRepository<String, Member> {
    private final Map<String, Member> memberMap = new ConcurrentHashMap<>();
    @Override
    public void save(Member member) {
        memberMap.putIfAbsent(member.getId(), member);
    }

    @Override
    public Optional<Member> findById(String memberId) {
        return Optional.ofNullable(memberMap.get(memberId));
    }

    @Override
    public List<Member> findAll() {
        return memberMap.values().stream().toList();
    }

    @Override
    public Member delete(String memberId) {
        return memberMap.remove(memberId);
    }
}
