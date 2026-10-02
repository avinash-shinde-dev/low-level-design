package com.shikavani.lld.librarymanagement.notification;

import com.shikavani.lld.librarymanagement.models.Hold;
import com.shikavani.lld.librarymanagement.models.Member;
import com.shikavani.lld.librarymanagement.models.Transaction;

public sealed interface LibraryEvent {
    record DueSoon(Member member, Transaction transaction) implements LibraryEvent {}
    record OverDue(Member member, Transaction transaction) implements LibraryEvent {}
    record HoldReady(Member member, Hold hold) implements LibraryEvent {}
    record HoldExpired(Member member, Hold hold) implements LibraryEvent {}
}
