package com.shikavani.lld.librarymanagement.repository;

import com.shikavani.lld.librarymanagement.models.Branch;

public class BranchRepository extends InMemoryRepository<Branch> {
    public BranchRepository() { super(Branch::id); }
}
