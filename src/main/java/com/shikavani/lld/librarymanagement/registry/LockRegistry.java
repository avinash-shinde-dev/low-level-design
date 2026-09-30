package com.shikavani.lld.librarymanagement.registry;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public final class LockRegistry {

    private final Map<String, Lock> registry = new ConcurrentHashMap<>();
    private volatile static LockRegistry lock;
    private LockRegistry() {}
    public Lock member(String memberId) { return get("M: " + memberId); }

    public Lock title(String title) { return get("T: " + title); }

    public Lock bookCopy(String copyId) { return get("B: " + copyId); }

    private Lock get(String key){
        return registry.computeIfAbsent(key, k -> new ReentrantLock());
    }

    // Double check thread safe implementation
    public static LockRegistry getInstance() {
        if (lock == null){
            synchronized (LockRegistry.class){
                if(lock == null)
                    lock = new LockRegistry();
            }
        }
        return lock;
    }

}