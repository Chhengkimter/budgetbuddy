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

    // ── Get all users ─────────────────────────────────
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // ── Get user by ID ────────────────────────────────
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    // ── Create a new user ─────────────────────────────
    public User createUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already in use: " + user.getEmail());
        }
        // NOTE: In a real app, hash the password before saving!
        // e.g. user.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));
        return userRepository.save(user);
    }

    // ── Update an existing user ───────────────────────
    public User updateUser(Long id, User updatedUser) {
        User existing = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        existing.setName(updatedUser.getName());
        existing.setEmail(updatedUser.getEmail());
        return userRepository.save(existing);
    }

    // ── Delete a user ─────────────────────────────────
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

    // ── Login (simple check) ──────────────────────────
    public Optional<User> login(String email, String password) {
        return userRepository.findByEmail(email)
            .filter(user -> user.getPassword().equals(password));
    }
}
