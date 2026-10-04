package com.shikavani.lld.librarymanagement.registry;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Hands out ONE lock per member / per book / per copy (not one global lock).
 *
 * LOCK ORDER (always follow this to avoid deadlocks):  member  ->  book  ->  copy
 * A thread may skip a level, but must never take them in a different order.
 * Locks are reentrant, so a method can lock something its caller already locked.
 */
public final class LockRegistry {
    private static final LockRegistry INSTANCE = new LockRegistry();
    private final Map<String, Lock> locks = new ConcurrentHashMap<>();

    private LockRegistry() { }

    public static LockRegistry getInstance() { return INSTANCE; }

    public Lock member(String memberId)  { return get("member:" + memberId); }
    public Lock book(String bookId)      { return get("book:" + bookId); }
    public Lock bookCopy(String copyId)  { return get("copy:" + copyId); }

    private Lock get(String key) {
        return locks.computeIfAbsent(key, k -> new ReentrantLock());
    }
}
