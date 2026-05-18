package com.budget.app.service;

import com.budget.app.model.UserSettings;
import com.budget.app.repository.UserSettingsRepository;
import com.budget.app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UserSettingsService {

    @Autowired
    private UserSettingsRepository userSettingsRepository;

    @Autowired
    private UserRepository userRepository;

    // ── Get settings by user ID ──────────────────────────
    public Optional<UserSettings> getSettingsByUserId(Long userId) {
        return userSettingsRepository.findByUserId(userId);
    }

    // ── Get settings by ID ───────────────────────────────
    public Optional<UserSettings> getSettingsById(Long id) {
        return userSettingsRepository.findById(id);
    }

    // ── Create settings for a user ───────────────────────
    public UserSettings createSettings(Long userId, UserSettings settings) {
        // Check if user exists
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("User not found with id: " + userId);
        }

        // Check if settings already exist
        if (userSettingsRepository.existsByUserId(userId)) {
            throw new RuntimeException("Settings already exist for user with id: " + userId);
        }

        settings.setUserId(userId);
        settings.setUpdatedAt(LocalDateTime.now());
        return userSettingsRepository.save(settings);
    }

    // ── Create default settings for a user ───────────────
    public UserSettings createDefaultSettings(Long userId) {
        // Check if user exists
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("User not found with id: " + userId);
        }

        // Check if settings already exist
        if (userSettingsRepository.existsByUserId(userId)) {
            throw new RuntimeException("Settings already exist for user with id: " + userId);
        }

        UserSettings defaultSettings = new UserSettings(userId);
        return userSettingsRepository.save(defaultSettings);
    }

    // ── Update settings ──────────────────────────────────
    public UserSettings updateSettings(Long userId, UserSettings updatedSettings) {
        UserSettings existing = userSettingsRepository.findByUserId(userId)
            .orElseThrow(() -> new RuntimeException("Settings not found for user with id: " + userId));

        // Update individual fields to preserve other settings
        if (updatedSettings.getCurrency() != null) {
            existing.setCurrency(updatedSettings.getCurrency());
        }
        if (updatedSettings.getLanguage() != null) {
            existing.setLanguage(updatedSettings.getLanguage());
        }
        if (updatedSettings.getEmailAlerts() != null) {
            existing.setEmailAlerts(updatedSettings.getEmailAlerts());
        }
        if (updatedSettings.getBudgetAlerts() != null) {
            existing.setBudgetAlerts(updatedSettings.getBudgetAlerts());
        }
        if (updatedSettings.getBudgetAlertThreshold() != null) {
            existing.setBudgetAlertThreshold(updatedSettings.getBudgetAlertThreshold());
        }

        existing.setUpdatedAt(LocalDateTime.now());
        return userSettingsRepository.save(existing);
    }

    // ── Update currency ─────────────────────────────────
    public UserSettings updateCurrency(Long userId, String currency) {
        UserSettings existing = userSettingsRepository.findByUserId(userId)
            .orElseThrow(() -> new RuntimeException("Settings not found for user with id: " + userId));
        existing.setCurrency(currency);
        existing.setUpdatedAt(LocalDateTime.now());
        return userSettingsRepository.save(existing);
    }

    // ── Update language ─────────────────────────────────
    public UserSettings updateLanguage(Long userId, String language) {
        UserSettings existing = userSettingsRepository.findByUserId(userId)
            .orElseThrow(() -> new RuntimeException("Settings not found for user with id: " + userId));
        existing.setLanguage(language);
        existing.setUpdatedAt(LocalDateTime.now());
        return userSettingsRepository.save(existing);
    }

    // ── Update email alerts ──────────────────────────────
    public UserSettings updateEmailAlerts(Long userId, Boolean emailAlerts) {
        UserSettings existing = userSettingsRepository.findByUserId(userId)
            .orElseThrow(() -> new RuntimeException("Settings not found for user with id: " + userId));
        existing.setEmailAlerts(emailAlerts);
        existing.setUpdatedAt(LocalDateTime.now());
        return userSettingsRepository.save(existing);
    }

    // ── Update budget alerts ─────────────────────────────
    public UserSettings updateBudgetAlerts(Long userId, Boolean budgetAlerts) {
        UserSettings existing = userSettingsRepository.findByUserId(userId)
            .orElseThrow(() -> new RuntimeException("Settings not found for user with id: " + userId));
        existing.setBudgetAlerts(budgetAlerts);
        existing.setUpdatedAt(LocalDateTime.now());
        return userSettingsRepository.save(existing);
    }

    // ── Update budget alert threshold ────────────────────
    public UserSettings updateBudgetAlertThreshold(Long userId, BigDecimal threshold) {
        if (threshold.compareTo(BigDecimal.ZERO) < 0 || threshold.compareTo(new BigDecimal("100")) > 0) {
            throw new RuntimeException("Budget alert threshold must be between 0 and 100");
        }
        UserSettings existing = userSettingsRepository.findByUserId(userId)
            .orElseThrow(() -> new RuntimeException("Settings not found for user with id: " + userId));
        existing.setBudgetAlertThreshold(threshold);
        existing.setUpdatedAt(LocalDateTime.now());
        return userSettingsRepository.save(existing);
    }

    // ── Delete settings ──────────────────────────────────
    public void deleteSettings(Long userId) {
        if (!userSettingsRepository.existsByUserId(userId)) {
            throw new RuntimeException("Settings not found for user with id: " + userId);
        }
        UserSettings settings = userSettingsRepository.findByUserId(userId).get();
        userSettingsRepository.delete(settings);
    }
}
