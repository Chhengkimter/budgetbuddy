package com.budget.app.repository;

import com.budget.app.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

// JpaRepository gives us free CRUD methods:
// save(), findById(), findAll(), deleteById(), etc.

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Spring auto-generates SQL for these based on method name!
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
}
