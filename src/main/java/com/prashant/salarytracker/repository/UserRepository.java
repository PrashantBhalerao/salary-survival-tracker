package com.prashant.salarytracker.repository;

import com.prashant.salarytracker.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByEmailAndPassword(String email, String password);
    long count();

}