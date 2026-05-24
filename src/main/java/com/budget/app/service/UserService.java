package com.budget.app.service;

import com.budget.app.dto.UserDTO;
import com.budget.app.model.User;
import com.budget.app.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;  // BCrypt injected from SecurityConfig

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // ── Create ────────────────────────────────────────────────────────────────

    public UserDTO.Response createUser(UserDTO.Request request) {
        if (userRepository.existsByUserEmail(request.getUserEmail())) {
            throw new RuntimeException("Email already in use: " + request.getUserEmail());
        }

        User user = new User();
        user.setUserFirstName(request.getUserFirstName());
        user.setUserLastName(request.getUserLastName());
        user.setUserEmail(request.getUserEmail());
        user.setUserPhoneNumber(request.getUserPhoneNumber());

        // ✅ Hash the password before saving — plain text never touches the DB
        user.setUserPassword(passwordEncoder.encode(request.getUserPassword()));

        return toResponse(userRepository.save(user));
    }

    // ── Login (hash input → compare against stored hash) ─────────────────────

    public UserDTO.Response login(String email, String rawPassword) {

        // Step 1: find user by email
        User user = userRepository.findByUserEmail(email)
                .orElseThrow(() -> new RuntimeException("Invalid email or password."));

        // Step 2: check account is active
        if (!user.getUserIsActive()) {
            throw new RuntimeException("Account is deactivated.");
        }

        // Step 3: BCrypt hashes rawPassword and compares with stored hash
        // passwordEncoder.matches("abc123", "$2a$12$...") → true or false
        boolean passwordMatches = passwordEncoder.matches(rawPassword, user.getUserPassword());
        if (!passwordMatches) {
            throw new RuntimeException("Invalid email or password.");
        }

        return toResponse(user);
    }

    // ── Read ──────────────────────────────────────────────────────────────────

    public List<UserDTO.Response> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public UserDTO.Response getUserByID(Long userID) {
        User user = userRepository.findById(userID)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userID));
        return toResponse(user);
    }

    // ── Update ────────────────────────────────────────────────────────────────

    public UserDTO.Response updateUser(Long userID, UserDTO.Request request) {
        User user = userRepository.findById(userID)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userID));

        user.setUserFirstName(request.getUserFirstName());
        user.setUserLastName(request.getUserLastName());
        user.setUserEmail(request.getUserEmail());
        user.setUserPhoneNumber(request.getUserPhoneNumber());

        // Only re-hash and update password if a new one was actually sent
        if (request.getUserPassword() != null && !request.getUserPassword().isBlank()) {
            user.setUserPassword(passwordEncoder.encode(request.getUserPassword()));
        }

        return toResponse(userRepository.save(user));
    }

    // ── Soft Delete ───────────────────────────────────────────────────────────

    public void deactivateUser(Long userID) {
        User user = userRepository.findById(userID)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userID));
        user.setUserIsActive(false);
        userRepository.save(user);
    }

    // ── Mapper ────────────────────────────────────────────────────────────────

    private UserDTO.Response toResponse(User user) {
        UserDTO.Response response = new UserDTO.Response();
        response.setUserID(user.getUserID());
        response.setUserFirstName(user.getUserFirstName());
        response.setUserLastName(user.getUserLastName());
        response.setUserEmail(user.getUserEmail());
        response.setUserPhoneNumber(user.getUserPhoneNumber());
        response.setUserCreated(user.getUserCreated());
        response.setUserIsActive(user.getUserIsActive());
        // Password never mapped — never leaves the backend
        return response;
    }
}