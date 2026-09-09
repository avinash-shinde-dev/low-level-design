package com.shikavani.lld.parkinglot.repository;

import com.shikavani.lld.parkinglot.model.User;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UserRepository implements InMemoryRepository<String, User>{
    private final Map<String, User> userMap = new ConcurrentHashMap<>();
    @Override
    public User save(User user) {
        userMap.put(user.id(), user);
        return user;
    }

    @Override
    public User findById(String id) {
        return userMap.get(id);
    }

    @Override
    public List<User> findAll() {
        return userMap.values().stream().toList();
    }
}
