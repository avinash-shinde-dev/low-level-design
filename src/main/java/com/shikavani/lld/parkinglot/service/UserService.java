package com.shikavani.lld.parkinglot.service;

import com.shikavani.lld.parkinglot.model.User;
import com.shikavani.lld.parkinglot.repository.UserRepository;

import java.util.List;

public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User addUser(User user){
        return userRepository.save(user);
    }

    public User getUser(String id){
        return this.userRepository.findById(id);
    }

    public List<User> getAllUsers(){
        return this.userRepository.findAll();
    }
}
