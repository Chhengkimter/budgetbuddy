package com.budget.app.controller;

import com.budget.app.model.User;
import com.budget.app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

// Handles all HTTP requests related to users (register, login, update, delete)
@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;

    // ── GET /api/users ────────────────────────────────
    // Returns all users in the database
    // Useful for admin/testing purposes
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    // ── GET /api/users/{id} ───────────────────────────
    // Returns one specific user by their ID
    // Example: GET /api/users/1
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return userService.getUserById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    // ── POST /api/users/register ──────────────────────
    // Creates a new user account
    // Request body must include: name, email, password
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody User user) {
        try {
            User created = userService.createUser(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ── POST /api/users/login ─────────────────────────
    // Checks email and password, returns the user object if correct
    // Request body must include: email, password
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody Map<String, String> credentials) {
        String email    = credentials.get("email");
        String password = credentials.get("password");
        return userService.login(email, password)
            .map(user -> ResponseEntity.ok((Object) user))
            .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "Invalid email or password")));
    }

    // ── PUT /api/users/{id} ───────────────────────────
    // Updates a user's name or email
    // Example: PUT /api/users/1
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @Valid @RequestBody User user) {
        try {
            return ResponseEntity.ok(userService.updateUser(id, user));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ── DELETE /api/users/{id} ────────────────────────
    // Soft deletes a user (sets is_active = false instead of removing from DB)
    // Example: DELETE /api/users/1
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.ok(Map.of("message", "User deactivated successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
