package com.budget.app.controller;

import com.budget.app.dto.BudgetRequestDTO;
import com.budget.app.dto.BudgetSummaryDTO;
import com.budget.app.model.Budget;
import com.budget.app.service.BudgetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for budget plans.
 *
 * Endpoints:
 *   GET    /api/budgets/summary?userID=&month=&year=   → BudgetSummaryDTO
 *   POST   /api/budgets?userID=                        → Budget (201)
 *   PUT    /api/budgets/{budgetID}                     → Budget
 *   DELETE /api/budgets/{budgetID}                     → 204
 */
@RestController
@RequestMapping("/api/budgets")
@CrossOrigin(origins = "*")
public class BudgetController {

    private final BudgetService budgetService;

    @Autowired
    public BudgetController(BudgetService budgetService) {
        this.budgetService = budgetService;
    }

    // ── Summary (used by budget.html) ─────────────────────────────────────────

    @GetMapping("/summary")
    public ResponseEntity<BudgetSummaryDTO> getSummary(
            @RequestParam Long userID,
            @RequestParam int  month,
            @RequestParam int  year) {
        return ResponseEntity.ok(budgetService.getSummary(userID, month, year));
    }

    // ── Create ────────────────────────────────────────────────────────────────

    @PostMapping
    public ResponseEntity<Budget> createBudget(
            @RequestParam Long userID,
            @RequestBody  BudgetRequestDTO req) {
        Budget created = budgetService.createBudget(userID, req);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // ── Update ────────────────────────────────────────────────────────────────

    @PutMapping("/{budgetID}")
    public ResponseEntity<Budget> updateBudget(
            @PathVariable Long budgetID,
            @RequestBody  BudgetRequestDTO req) {
        return ResponseEntity.ok(budgetService.updateBudget(budgetID, req));
    }

    // ── Delete ────────────────────────────────────────────────────────────────

    @DeleteMapping("/{budgetID}")
    public ResponseEntity<Void> deleteBudget(@PathVariable Long budgetID) {
        budgetService.deleteBudget(budgetID);
        return ResponseEntity.noContent().build();
    }
}