package com.budget.app.repository;

import com.budget.app.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Check if email already registered
    boolean existsByUserEmail(String userEmail);

    // Find user by email (used for login later)
    Optional<User> findByUserEmail(String userEmail);
}