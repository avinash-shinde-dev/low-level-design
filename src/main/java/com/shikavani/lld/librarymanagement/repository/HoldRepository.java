package com.shikavani.lld.librarymanagement.repository;

import com.shikavani.lld.librarymanagement.models.Hold;

public class HoldRepository extends InMemoryRepository<Hold> {
    public HoldRepository() { super(Hold::getHoldId); }
}
