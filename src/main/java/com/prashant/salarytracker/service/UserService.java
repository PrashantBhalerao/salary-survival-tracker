package com.prashant.salarytracker.service;

import com.prashant.salarytracker.model.User;
import com.prashant.salarytracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public void saveUser(User user) {
        userRepository.save(user);
    }

    public User loginUser(String email, String password) {

        return userRepository.findByEmailAndPassword(email, password);
    }

    // TOTAL USERS

    public long getTotalUsers() {

        return userRepository.count();
    }
}