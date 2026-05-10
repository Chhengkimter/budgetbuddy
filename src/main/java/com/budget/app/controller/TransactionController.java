package com.budget.app.controller;

import com.budget.app.model.Transaction;
import com.budget.app.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/transactions")
@CrossOrigin(origins = "*")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    // GET /api/transactions/budget/{budgetId}
    @GetMapping("/budget/{budgetId}")
    public ResponseEntity<List<Transaction>> getByBudget(@PathVariable Long budgetId) {
        return ResponseEntity.ok(transactionService.getTransactionsByBudget(budgetId));
    }

    // GET /api/transactions/user/{userId}
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Transaction>> getByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(transactionService.getTransactionsByUser(userId));
    }

    // POST /api/transactions/budget/{budgetId}
    @PostMapping("/budget/{budgetId}")
    public ResponseEntity<?> addTransaction(@PathVariable Long budgetId,
                                             @Valid @RequestBody Transaction transaction) {
        try {
            Transaction created = transactionService.addTransaction(budgetId, transaction);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // DELETE /api/transactions/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTransaction(@PathVariable Long id) {
        try {
            transactionService.deleteTransaction(id);
            return ResponseEntity.ok(Map.of("message", "Transaction deleted successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
