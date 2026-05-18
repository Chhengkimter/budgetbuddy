package com.budget.app.controller;

import com.budget.app.model.RecurringTransaction;
import com.budget.app.model.RecurringTransactionFrequency;
import com.budget.app.model.Transaction;
import com.budget.app.service.RecurringTransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/recurring-transactions")
@CrossOrigin(origins = "*")
public class RecurringTransactionController {

    @Autowired
    private RecurringTransactionService recurringTransactionService;

    // ── GET /api/recurring-transactions/budget/{budgetId} ────────
    @GetMapping("/budget/{budgetId}")
    public ResponseEntity<List<RecurringTransaction>> getRecurringTransactionsByBudget(@PathVariable Long budgetId) {
        return ResponseEntity.ok(recurringTransactionService.getRecurringTransactionsByBudget(budgetId));
    }

    // ── GET /api/recurring-transactions/budget/{budgetId}/active ─
    @GetMapping("/budget/{budgetId}/active")
    public ResponseEntity<List<RecurringTransaction>> getActiveRecurringTransactionsByBudget(@PathVariable Long budgetId) {
        return ResponseEntity.ok(recurringTransactionService.getActiveRecurringTransactionsByBudget(budgetId));
    }

    // ── GET /api/recurring-transactions/budget/{budgetId}/due ────
    @GetMapping("/budget/{budgetId}/due")
    public ResponseEntity<List<RecurringTransaction>> getDueRecurringTransactions(@PathVariable Long budgetId) {
        return ResponseEntity.ok(recurringTransactionService.getDueRecurringTransactions(budgetId));
    }

    // ── GET /api/recurring-transactions/user/{userId}/due ────────
    @GetMapping("/user/{userId}/due")
    public ResponseEntity<List<RecurringTransaction>> getDueRecurringTransactionsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(recurringTransactionService.getDueRecurringTransactionsByUser(userId));
    }

    // ── GET /api/recurring-transactions/user/{userId} ──────────
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<RecurringTransaction>> getRecurringTransactionsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(recurringTransactionService.getRecurringTransactionsByUser(userId));
    }

    // ── GET /api/recurring-transactions/{id} ────────────────────
    @GetMapping("/{id}")
    public ResponseEntity<RecurringTransaction> getRecurringTransactionById(@PathVariable Long id) {
        return recurringTransactionService.getRecurringTransactionById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    // ── GET /api/recurring-transactions/budget/{budgetId}/type/{type} ─
    @GetMapping("/budget/{budgetId}/type/{type}")
    public ResponseEntity<List<RecurringTransaction>> getRecurringTransactionsByType(@PathVariable Long budgetId,
                                                                                      @PathVariable String type) {
        try {
            Transaction.Type transactionType = Transaction.Type.valueOf(type.toUpperCase());
            return ResponseEntity.ok(recurringTransactionService.getRecurringTransactionsByType(budgetId, transactionType));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // ── GET /api/recurring-transactions/budget/{budgetId}/frequency/{freq} ──
    @GetMapping("/budget/{budgetId}/frequency/{freq}")
    public ResponseEntity<List<RecurringTransaction>> getRecurringTransactionsByFrequency(@PathVariable Long budgetId,
                                                                                           @PathVariable String freq) {
        try {
            RecurringTransactionFrequency frequency = RecurringTransactionFrequency.valueOf(freq.toUpperCase());
            return ResponseEntity.ok(recurringTransactionService.getRecurringTransactionsByFrequency(budgetId, frequency));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // ── POST /api/recurring-transactions/budget/{budgetId} ──────
    @PostMapping("/budget/{budgetId}")
    public ResponseEntity<?> createRecurringTransaction(@PathVariable Long budgetId,
                                                         @Valid @RequestBody RecurringTransaction transaction) {
        try {
            RecurringTransaction created = recurringTransactionService.createRecurringTransaction(budgetId, transaction);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ── PUT /api/recurring-transactions/{id} ────────────────────
    @PutMapping("/{id}")
    public ResponseEntity<?> updateRecurringTransaction(@PathVariable Long id,
                                                         @Valid @RequestBody RecurringTransaction transaction) {
        try {
            return ResponseEntity.ok(recurringTransactionService.updateRecurringTransaction(id, transaction));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ── PUT /api/recurring-transactions/{id}/advance ────────────
    @PutMapping("/{id}/advance")
    public ResponseEntity<?> advanceToNextOccurrence(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(recurringTransactionService.advanceToNextOccurrence(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ── PUT /api/recurring-transactions/{id}/activate ───────────
    @PutMapping("/{id}/activate")
    public ResponseEntity<?> activate(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(recurringTransactionService.activate(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ── PUT /api/recurring-transactions/{id}/deactivate ────────
    @PutMapping("/{id}/deactivate")
    public ResponseEntity<?> deactivate(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(recurringTransactionService.deactivate(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ── PUT /api/recurring-transactions/{id}/toggle ─────────────
    @PutMapping("/{id}/toggle")
    public ResponseEntity<?> toggleActive(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(recurringTransactionService.toggleActive(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ── DELETE /api/recurring-transactions/{id} ─────────────────
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRecurringTransaction(@PathVariable Long id) {
        try {
            recurringTransactionService.deleteRecurringTransaction(id);
            return ResponseEntity.ok(Map.of("message", "Recurring transaction deleted successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ── GET /api/recurring-transactions/{id}/is-due ──────────────
    @GetMapping("/{id}/is-due")
    public ResponseEntity<Map<String, Boolean>> isRecurringTransactionDue(@PathVariable Long id) {
        boolean isDue = recurringTransactionService.isRecurringTransactionDue(id);
        return ResponseEntity.ok(Map.of("isDue", isDue));
    }

    // ── GET /api/recurring-transactions/{id}/is-active ────────────
    @GetMapping("/{id}/is-active")
    public ResponseEntity<Map<String, Boolean>> isRecurringTransactionActive(@PathVariable Long id) {
        boolean isActive = recurringTransactionService.isRecurringTransactionActive(id);
        return ResponseEntity.ok(Map.of("isActive", isActive));
    }

    // ── GET /api/recurring-transactions/budget/{budgetId}/count ───
    @GetMapping("/budget/{budgetId}/count")
    public ResponseEntity<Map<String, Long>> countActiveRecurringTransactions(@PathVariable Long budgetId) {
        Long count = recurringTransactionService.countActiveRecurringTransactions(budgetId);
        return ResponseEntity.ok(Map.of("activeCount", count));
    }
}
