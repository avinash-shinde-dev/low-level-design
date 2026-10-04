package com.shikavani.lld.librarymanagement.repository;

import com.shikavani.lld.librarymanagement.models.Member;

public class MemberRepository extends InMemoryRepository<Member> {
    public MemberRepository() { super(Member::getId); }
}
