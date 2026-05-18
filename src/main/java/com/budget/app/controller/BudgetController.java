package com.budget.app.controller;

import com.budget.app.model.Budget;
import com.budget.app.service.BudgetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

// @RestController  = this class handles HTTP requests and returns JSON
// @RequestMapping  = all endpoints in this class start with /api/budgets
// @CrossOrigin     = allows your HTML frontend to call this API
@RestController
@RequestMapping("/api/budgets")
@CrossOrigin(origins = "*")
public class BudgetController {

    // Spring automatically creates and injects BudgetService for us
    @Autowired
    private BudgetService budgetService;

    // ── GET /api/budgets/user/{userId} ────────────────
    // Returns all budgets belonging to one user
    // Example: GET /api/budgets/user/1
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Budget>> getBudgetsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(budgetService.getBudgetsByUser(userId));
    }

    // ── GET /api/budgets/{id} ─────────────────────────
    // Returns one specific budget by its ID
    // Example: GET /api/budgets/3
    @GetMapping("/{id}")
    public ResponseEntity<Budget> getBudgetById(@PathVariable Long id) {
        return budgetService.getBudgetById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    // ── POST /api/budgets/user/{userId} ───────────────
    // Creates a new budget for a user
    // Request body must include: name, totalAmount, category
    // Optional body fields: description
    @PostMapping("/user/{userId}")
    public ResponseEntity<?> createBudget(@PathVariable Long userId,
                                           @Valid @RequestBody Budget budget) {
        try {
            Budget created = budgetService.createBudget(userId, budget);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ── PUT /api/budgets/{id} ─────────────────────────
    // Updates an existing budget's name, amount, category, or description
    // Example: PUT /api/budgets/3
    @PutMapping("/{id}")
    public ResponseEntity<?> updateBudget(@PathVariable Long id,
                                           @Valid @RequestBody Budget budget) {
        try {
            return ResponseEntity.ok(budgetService.updateBudget(id, budget));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ── DELETE /api/budgets/{id} ──────────────────────
    // Deletes a budget and all its transactions (cascade delete in DB)
    // Example: DELETE /api/budgets/3
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBudget(@PathVariable Long id) {
        try {
            budgetService.deleteBudget(id);
            return ResponseEntity.ok(Map.of("message", "Budget deleted successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
