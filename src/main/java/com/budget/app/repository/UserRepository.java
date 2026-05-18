package com.budget.app.repository;

import com.budget.app.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Find a user by their email (used for login)
    // SQL: SELECT * FROM users WHERE email = ?
    Optional<User> findByEmail(String email);

    // Check if an email already exists (used during registration)
    // SQL: SELECT COUNT(*) FROM users WHERE email = ?
    boolean existsByEmail(String email);

    // UPDATED: get only active users (is_active = true)
    // SQL: SELECT * FROM users WHERE is_active = true
    List<User> findByIsActiveTrue();
}
