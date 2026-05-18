package com.budget.app.controller;

import com.budget.app.model.SavingsGoal;
import com.budget.app.model.SavingsGoalStatus;
import com.budget.app.service.SavingsGoalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/savings-goals")
@CrossOrigin(origins = "*")
public class SavingsGoalController {

    @Autowired
    private SavingsGoalService savingsGoalService;

    // GET /api/savings-goals/user/{userId}
    /**
     * Get all savings goals for a user
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<SavingsGoal>> getSavingsGoalsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(savingsGoalService.getSavingsGoalsByUser(userId));
    }

    // GET /api/savings-goals/{id}
    /**
     * Get a single savings goal by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<SavingsGoal> getSavingsGoalById(@PathVariable Long id) {
        return savingsGoalService.getSavingsGoalById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    // GET /api/savings-goals/user/{userId}/goal/{id}
    /**
     * Get a specific savings goal for a user (with ownership verification)
     */
    @GetMapping("/user/{userId}/goal/{id}")
    public ResponseEntity<SavingsGoal> getSavingsGoalByIdAndUser(
            @PathVariable Long id,
            @PathVariable Long userId) {
        return savingsGoalService.getSavingsGoalByIdAndUser(id, userId)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    // GET /api/savings-goals/user/{userId}/status/{status}
    /**
     * Get all savings goals for a user with a specific status
     */
    @GetMapping("/user/{userId}/status/{status}")
    public ResponseEntity<?> getSavingsGoalsByUserAndStatus(
            @PathVariable Long userId,
            @PathVariable String status) {
        try {
            SavingsGoalStatus goalStatus = SavingsGoalStatus.valueOf(status.toUpperCase());
            return ResponseEntity.ok(savingsGoalService.getSavingsGoalsByUserAndStatus(userId, goalStatus));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid status: " + status));
        }
    }

    // GET /api/savings-goals/user/{userId}/active
    /**
     * Get all active (IN_PROGRESS) savings goals for a user
     */
    @GetMapping("/user/{userId}/active")
    public ResponseEntity<List<SavingsGoal>> getActiveSavingsGoals(@PathVariable Long userId) {
        return ResponseEntity.ok(savingsGoalService.getActiveSavingsGoals(userId));
    }

    // GET /api/savings-goals/user/{userId}/completed
    /**
     * Get all completed savings goals for a user
     */
    @GetMapping("/user/{userId}/completed")
    public ResponseEntity<List<SavingsGoal>> getCompletedSavingsGoals(@PathVariable Long userId) {
        return ResponseEntity.ok(savingsGoalService.getCompletedSavingsGoals(userId));
    }

    // GET /api/savings-goals/user/{userId}/cancelled
    /**
     * Get all cancelled savings goals for a user
     */
    @GetMapping("/user/{userId}/cancelled")
    public ResponseEntity<List<SavingsGoal>> getCancelledSavingsGoals(@PathVariable Long userId) {
        return ResponseEntity.ok(savingsGoalService.getCancelledSavingsGoals(userId));
    }

    // GET /api/savings-goals/user/{userId}/date-range
    /**
     * Get savings goals within a target date range
     * Query params: startDate, endDate (ISO date format: YYYY-MM-DD)
     */
    @GetMapping("/user/{userId}/date-range")
    public ResponseEntity<?> getSavingsGoalsByDateRange(
            @PathVariable Long userId,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {
        if (startDate.isAfter(endDate)) {
            return ResponseEntity.badRequest().body(Map.of("error", "Start date must be before end date"));
        }
        return ResponseEntity.ok(savingsGoalService.getSavingsGoalsByDateRange(userId, startDate, endDate));
    }

    // POST /api/savings-goals/user/{userId}
    /**
     * Create a new savings goal
     */
    @PostMapping("/user/{userId}")
    public ResponseEntity<?> createSavingsGoal(@PathVariable Long userId,
                                                @Valid @RequestBody SavingsGoal savingsGoal) {
        try {
            SavingsGoal created = savingsGoalService.createSavingsGoal(userId, savingsGoal);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // PUT /api/savings-goals/{id}
    /**
     * Update a savings goal
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateSavingsGoal(@PathVariable Long id,
                                                @Valid @RequestBody SavingsGoal savingsGoal) {
        try {
            return ResponseEntity.ok(savingsGoalService.updateSavingsGoal(id, savingsGoal));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // PATCH /api/savings-goals/{id}/target-amount
    /**
     * Update the target amount of a savings goal
     */
    @PatchMapping("/{id}/target-amount")
    public ResponseEntity<?> updateTargetAmount(@PathVariable Long id,
                                                 @RequestBody Map<String, BigDecimal> request) {
        try {
            BigDecimal newAmount = request.get("targetAmount");
            if (newAmount == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "targetAmount is required"));
            }
            return ResponseEntity.ok(savingsGoalService.updateTargetAmount(id, newAmount));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // PATCH /api/savings-goals/{id}/status
    /**
     * Update the status of a savings goal
     * Request body: { "status": "IN_PROGRESS" | "COMPLETED" | "CANCELLED" }
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Long id,
                                          @RequestBody Map<String, String> request) {
        try {
            String statusStr = request.get("status");
            if (statusStr == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "status is required"));
            }
            SavingsGoalStatus newStatus = SavingsGoalStatus.valueOf(statusStr.toUpperCase());
            return ResponseEntity.ok(savingsGoalService.updateStatus(id, newStatus));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid status provided"));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // POST /api/savings-goals/{id}/add
    /**
     * Add savings/contribution to a goal
     * Request body: { "amount": 100.00 }
     */
    @PostMapping("/{id}/add")
    public ResponseEntity<?> addSavings(@PathVariable Long id,
                                        @RequestBody Map<String, BigDecimal> request) {
        try {
            BigDecimal amount = request.get("amount");
            if (amount == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "amount is required"));
            }
            return ResponseEntity.ok(savingsGoalService.addSavings(id, amount));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // POST /api/savings-goals/{id}/withdraw
    /**
     * Withdraw from a savings goal
     * Request body: { "amount": 50.00 }
     */
    @PostMapping("/{id}/withdraw")
    public ResponseEntity<?> withdrawSavings(@PathVariable Long id,
                                             @RequestBody Map<String, BigDecimal> request) {
        try {
            BigDecimal amount = request.get("amount");
            if (amount == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "amount is required"));
            }
            return ResponseEntity.ok(savingsGoalService.withdrawSavings(id, amount));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // DELETE /api/savings-goals/{id}
    /**
     * Delete a savings goal
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSavingsGoal(@PathVariable Long id) {
        try {
            savingsGoalService.deleteSavingsGoal(id);
            return ResponseEntity.ok(Map.of("message", "Savings goal deleted successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // GET /api/savings-goals/user/{userId}/stats/count
    /**
     * Get count of total savings goals for a user
     */
    @GetMapping("/user/{userId}/stats/count")
    public ResponseEntity<Map<String, Object>> countSavingsGoalsByUser(@PathVariable Long userId) {
        Long count = savingsGoalService.countSavingsGoalsByUser(userId);
        return ResponseEntity.ok(Map.of("userId", userId, "totalGoals", count));
    }

    // GET /api/savings-goals/user/{userId}/stats/summary
    /**
     * Get comprehensive savings statistics for a user
     */
    @GetMapping("/user/{userId}/stats/summary")
    public ResponseEntity<Map<String, Object>> getSavingsSummary(@PathVariable Long userId) {
        return ResponseEntity.ok(Map.of(
            "userId", userId,
            "totalGoals", savingsGoalService.countSavingsGoalsByUser(userId),
            "totalSaved", savingsGoalService.getTotalSavedAmount(userId),
            "totalTarget", savingsGoalService.getTotalTargetAmount(userId),
            "totalRemaining", savingsGoalService.getTotalRemainingAmount(userId),
            "completedGoals", savingsGoalService.countCompletedGoals(userId),
            "activeGoals", savingsGoalService.countActiveGoals(userId),
            "averageProgress", savingsGoalService.getAverageProgressPercentage(userId)
        ));
    }

    // GET /api/savings-goals/user/{userId}/stats/total-saved
    /**
     * Get total saved amount across all goals for a user
     */
    @GetMapping("/user/{userId}/stats/total-saved")
    public ResponseEntity<Map<String, Object>> getTotalSavedAmount(@PathVariable Long userId) {
        return ResponseEntity.ok(Map.of(
            "userId", userId,
            "totalSaved", savingsGoalService.getTotalSavedAmount(userId)
        ));
    }

    // GET /api/savings-goals/user/{userId}/stats/total-target
    /**
     * Get total target amount across all goals for a user
     */
    @GetMapping("/user/{userId}/stats/total-target")
    public ResponseEntity<Map<String, Object>> getTotalTargetAmount(@PathVariable Long userId) {
        return ResponseEntity.ok(Map.of(
            "userId", userId,
            "totalTarget", savingsGoalService.getTotalTargetAmount(userId)
        ));
    }

    // GET /api/savings-goals/user/{userId}/stats/total-remaining
    /**
     * Get total remaining amount across all goals for a user
     */
    @GetMapping("/user/{userId}/stats/total-remaining")
    public ResponseEntity<Map<String, Object>> getTotalRemainingAmount(@PathVariable Long userId) {
        return ResponseEntity.ok(Map.of(
            "userId", userId,
            "totalRemaining", savingsGoalService.getTotalRemainingAmount(userId)
        ));
    }

    // GET /api/savings-goals/user/{userId}/stats/completed-count
    /**
     * Get count of completed goals for a user
     */
    @GetMapping("/user/{userId}/stats/completed-count")
    public ResponseEntity<Map<String, Object>> countCompletedGoals(@PathVariable Long userId) {
        return ResponseEntity.ok(Map.of(
            "userId", userId,
            "completedGoals", savingsGoalService.countCompletedGoals(userId)
        ));
    }

    // GET /api/savings-goals/user/{userId}/stats/active-count
    /**
     * Get count of active goals for a user
     */
    @GetMapping("/user/{userId}/stats/active-count")
    public ResponseEntity<Map<String, Object>> countActiveGoals(@PathVariable Long userId) {
        return ResponseEntity.ok(Map.of(
            "userId", userId,
            "activeGoals", savingsGoalService.countActiveGoals(userId)
        ));
    }

    // GET /api/savings-goals/user/{userId}/stats/average-progress
    /**
     * Get average progress percentage across all goals for a user
     */
    @GetMapping("/user/{userId}/stats/average-progress")
    public ResponseEntity<Map<String, Object>> getAverageProgressPercentage(@PathVariable Long userId) {
        return ResponseEntity.ok(Map.of(
            "userId", userId,
            "averageProgress", savingsGoalService.getAverageProgressPercentage(userId)
        ));
    }
}
