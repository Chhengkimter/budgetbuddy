package com.budget.app.service;

import com.budget.app.model.User;
import com.budget.app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // ── Get all active users ──────────────────────────
    // UPDATED: only returns users where is_active = true
    public List<User> getAllUsers() {
        return userRepository.findByIsActiveTrue();
    }

    // ── Get user by ID ────────────────────────────────
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    // ── Create a new user ─────────────────────────────
    // Checks for duplicate email before saving
    public User createUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already in use: " + user.getEmail());
        }
        // NOTE: In a real app, hash the password before saving!
        // e.g. user.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));
        return userRepository.save(user);
    }

    // ── Update an existing user ───────────────────────
    // Only updates name and email — password has its own flow
    public User updateUser(Long id, User updatedUser) {
        User existing = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        existing.setName(updatedUser.getName());
        existing.setEmail(updatedUser.getEmail());
        return userRepository.save(existing);
    }

    // ── Soft delete a user ────────────────────────────
    // UPDATED: sets is_active = false instead of removing from DB
    // We keep user data in the database for record keeping
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        user.setIsActive(false);
        userRepository.save(user);
    }

    // ── Login ─────────────────────────────────────────
    // Only allows active users to log in
    public Optional<User> login(String email, String password) {
        return userRepository.findByEmail(email)
            .filter(user -> user.getIsActive())                     // must be active
            .filter(user -> user.getPassword().equals(password));   // password must match
    }
}
