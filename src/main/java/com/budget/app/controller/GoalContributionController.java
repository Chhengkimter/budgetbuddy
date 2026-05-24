package com.budget.app.controller;

import com.budget.app.model.GoalContribution;
import com.budget.app.service.GoalContributionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/contributions")
@CrossOrigin(origins = "*")
public class GoalContributionController {

    @Autowired
    private GoalContributionService goalContributionService;

    // ── GET /api/contributions/goal/{goalId} ─────────
    @GetMapping("/goal/{goalId}")
    public ResponseEntity<List<GoalContribution>> getContributionsByGoal(@PathVariable Long goalId) {
        return ResponseEntity.ok(goalContributionService.getContributionsByGoal(goalId));
    }

    // ── GET /api/contributions/{id} ──────────────────
    @GetMapping("/{id}")
    public ResponseEntity<GoalContribution> getContributionById(@PathVariable Long id) {
        return goalContributionService.getContributionById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    // ── POST /api/contributions/goal/{goalId} ────────
    @PostMapping("/goal/{goalId}")
    public ResponseEntity<?> createContribution(@PathVariable Long goalId,
                                                 @Valid @RequestBody GoalContribution contribution) {
        try {
            GoalContribution created = goalContributionService.createContribution(goalId, contribution);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ── POST /api/contributions/goal/{goalId}/quick ──
    @PostMapping("/goal/{goalId}/quick")
    public ResponseEntity<?> quickContribution(@PathVariable Long goalId,
                                                @RequestBody Map<String, String> request) {
        try {
            String amountStr = request.get("amount");
            String notes = request.get("notes");

            if (amountStr == null || amountStr.isBlank()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Amount is required"));
            }

            BigDecimal amount = new BigDecimal(amountStr);
            GoalContribution created = goalContributionService.createContribution(goalId, amount, notes);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid amount format"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ── PUT /api/contributions/{id} ──────────────────
    @PutMapping("/{id}")
    public ResponseEntity<?> updateContribution(@PathVariable Long id,
                                                 @Valid @RequestBody GoalContribution contribution) {
        try {
            return ResponseEntity.ok(goalContributionService.updateContribution(id, contribution));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ── DELETE /api/contributions/{id} ───────────────
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteContribution(@PathVariable Long id) {
        try {
            goalContributionService.deleteContribution(id);
            return ResponseEntity.ok(Map.of("message", "Contribution deleted successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ── GET /api/contributions/goal/{goalId}/total ───
    @GetMapping("/goal/{goalId}/total")
    public ResponseEntity<Map<String, BigDecimal>> getTotalContributions(@PathVariable Long goalId) {
        BigDecimal total = goalContributionService.getTotalContributions(goalId);
        return ResponseEntity.ok(Map.of("total", total));
    }

    // ── GET /api/contributions/goal/{goalId}/count ───
    @GetMapping("/goal/{goalId}/count")
    public ResponseEntity<Map<String, Long>> countContributions(@PathVariable Long goalId) {
        Long count = goalContributionService.countContributions(goalId);
        return ResponseEntity.ok(Map.of("count", count));
    }

    // ── GET /api/contributions/goal/{goalId}/stats ───
    @GetMapping("/goal/{goalId}/stats")
    public ResponseEntity<?> getContributionStats(@PathVariable Long goalId) {
        try {
            BigDecimal total = goalContributionService.getTotalContributions(goalId);
            Long count = goalContributionService.countContributions(goalId);
            BigDecimal average = count > 0 ? total.divide(new BigDecimal(count), 2, java.math.RoundingMode.HALF_UP) : BigDecimal.ZERO;

            return ResponseEntity.ok(Map.of(
                "totalContributions", total,
                "numberOfContributions", count,
                "averageContribution", average
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
