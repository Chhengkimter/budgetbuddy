package com.budget.app.controller;

import com.budget.app.dto.BudgetDTO;
import com.budget.app.model.Budget;
import com.budget.app.service.BudgetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/budgets")
@CrossOrigin(origins = "*")
public class BudgetController {

    @Autowired
    private BudgetService budgetService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<BudgetDTO>> getBudgetsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(
            budgetService.getBudgetsByUser(userId)
                .stream()
                .map(BudgetDTO::new)
                .collect(Collectors.toList())
        );
    }

    @PostMapping("/user/{userId}")
    public ResponseEntity<BudgetDTO> createBudget(
            @PathVariable Long userId,
            @RequestBody Budget budget) {
        Budget saved = budgetService.createBudget(userId, budget);
        return ResponseEntity.status(201).body(new BudgetDTO(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateBudget(@PathVariable Long id,
                                           @Valid @RequestBody Budget budget) {
        try {
            Budget updated = budgetService.updateBudget(id, budget);
            return ResponseEntity.ok(new BudgetDTO(updated));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

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