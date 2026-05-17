package com.budget.app.controller;

import com.budget.app.model.UserSettings;
import com.budget.app.service.UserSettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/user-settings")
@CrossOrigin(origins = "*")
public class UserSettingsController {

    @Autowired
    private UserSettingsService userSettingsService;

    // ── GET /api/user-settings/user/{userId} ────────────
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getSettingsByUserId(@PathVariable Long userId) {
        return userSettingsService.getSettingsByUserId(userId)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    // ── GET /api/user-settings/{id} ──────────────────────
    @GetMapping("/{id}")
    public ResponseEntity<?> getSettingsById(@PathVariable Long id) {
        return userSettingsService.getSettingsById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    // ── POST /api/user-settings/user/{userId} ───────────
    @PostMapping("/user/{userId}")
    public ResponseEntity<?> createSettings(@PathVariable Long userId,
                                            @Valid @RequestBody UserSettings settings) {
        try {
            UserSettings created = userSettingsService.createSettings(userId, settings);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ── POST /api/user-settings/user/{userId}/default ───
    @PostMapping("/user/{userId}/default")
    public ResponseEntity<?> createDefaultSettings(@PathVariable Long userId) {
        try {
            UserSettings created = userSettingsService.createDefaultSettings(userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ── PUT /api/user-settings/user/{userId} ────────────
    @PutMapping("/user/{userId}")
    public ResponseEntity<?> updateSettings(@PathVariable Long userId,
                                            @Valid @RequestBody UserSettings settings) {
        try {
            return ResponseEntity.ok(userSettingsService.updateSettings(userId, settings));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ── PUT /api/user-settings/user/{userId}/currency ───
    @PutMapping("/user/{userId}/currency")
    public ResponseEntity<?> updateCurrency(@PathVariable Long userId,
                                            @RequestBody Map<String, String> request) {
        try {
            String currency = request.get("currency");
            if (currency == null || currency.isBlank()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Currency is required"));
            }
            return ResponseEntity.ok(userSettingsService.updateCurrency(userId, currency));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ── PUT /api/user-settings/user/{userId}/language ───
    @PutMapping("/user/{userId}/language")
    public ResponseEntity<?> updateLanguage(@PathVariable Long userId,
                                            @RequestBody Map<String, String> request) {
        try {
            String language = request.get("language");
            if (language == null || language.isBlank()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Language is required"));
            }
            return ResponseEntity.ok(userSettingsService.updateLanguage(userId, language));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ── PUT /api/user-settings/user/{userId}/email-alerts ──
    @PutMapping("/user/{userId}/email-alerts")
    public ResponseEntity<?> updateEmailAlerts(@PathVariable Long userId,
                                               @RequestBody Map<String, Boolean> request) {
        try {
            Boolean emailAlerts = request.get("emailAlerts");
            if (emailAlerts == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "emailAlerts is required"));
            }
            return ResponseEntity.ok(userSettingsService.updateEmailAlerts(userId, emailAlerts));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ── PUT /api/user-settings/user/{userId}/budget-alerts ──
    @PutMapping("/user/{userId}/budget-alerts")
    public ResponseEntity<?> updateBudgetAlerts(@PathVariable Long userId,
                                                @RequestBody Map<String, Boolean> request) {
        try {
            Boolean budgetAlerts = request.get("budgetAlerts");
            if (budgetAlerts == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "budgetAlerts is required"));
            }
            return ResponseEntity.ok(userSettingsService.updateBudgetAlerts(userId, budgetAlerts));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ── PUT /api/user-settings/user/{userId}/alert-threshold ──
    @PutMapping("/user/{userId}/alert-threshold")
    public ResponseEntity<?> updateBudgetAlertThreshold(@PathVariable Long userId,
                                                        @RequestBody Map<String, BigDecimal> request) {
        try {
            BigDecimal threshold = request.get("threshold");
            if (threshold == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Threshold is required"));
            }
            return ResponseEntity.ok(userSettingsService.updateBudgetAlertThreshold(userId, threshold));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ── DELETE /api/user-settings/user/{userId} ────────────
    @DeleteMapping("/user/{userId}")
    public ResponseEntity<?> deleteSettings(@PathVariable Long userId) {
        try {
            userSettingsService.deleteSettings(userId);
            return ResponseEntity.ok(Map.of("message", "Settings deleted successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
