package com.shikavani.lld.librarymanagement.repository;

import com.shikavani.lld.librarymanagement.models.Branch;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class BranchRepository implements InMemoryRepository<String, Branch>{
    private final Map<String, Branch> branchMap = new ConcurrentHashMap<>();
    public BranchRepository(){
        seedBranches();
    }
    @Override
    public void save(Branch branch) {
        this.branchMap.put(branch.id(), branch);
    }

    @Override
    public Optional<Branch> findById(String id) {
        return Optional.ofNullable(this.branchMap.get(id));
    }

    @Override
    public List<Branch> findAll() {
        return this.branchMap.values().stream().toList();
    }

    @Override
    public Branch delete(String id) {
        return this.branchMap.remove(id);
    }

    private void seedBranches(){
        List<Branch> branches = List.of(
                new Branch(UUID.randomUUID().toString(), "Library 101"),
                new Branch(UUID.randomUUID().toString(), "Library 102"),
                new Branch(UUID.randomUUID().toString(), "Library 103"),
                new Branch(UUID.randomUUID().toString(), "Library 104")
        );

        branches.forEach(branch -> {
            this.branchMap.put(branch.id(), branch);
        });
    }
}
