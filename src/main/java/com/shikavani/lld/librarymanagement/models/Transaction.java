package com.shikavani.lld.librarymanagement.models;

import com.shikavani.lld.librarymanagement.models.fine.FineBreakdown;

import java.time.LocalDateTime;

public record Transaction(String id, String bookCopyId, String bookId, String branchId, String memberId, LocalDateTime issuedAt, LocalDateTime dueAt, LocalDateTime returnTimeStamp, FineBreakdown fineBreakdown) {

    public Transaction closeTransaction(FineBreakdown fineBreakdown){
        return new Transaction(id, bookCopyId, bookId, branchId, memberId, issuedAt, dueAt, LocalDateTime.now(), fineBreakdown);
    }
}
