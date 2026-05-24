package com.budget.app.controller;

import com.budget.app.dto.TransactionRequestDTO;
import com.budget.app.dto.TransactionResponseDTO;
import com.budget.app.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for transactions.
 *
 * Endpoints:
 *   GET    /api/transactions?userID=&month=&year=           → List<TransactionResponseDTO> (all types)
 *   GET    /api/transactions?userID=&month=&year=&type=     → List<TransactionResponseDTO> (filtered)
 *         Accepted type values: INCOME | EXPENSE | SAVING | RECURRING
 *         RECURRING returns transactions whose source is a RecurringTransaction (recurringID != null)
 *   GET    /api/transactions/{id}?userID=                   → TransactionResponseDTO
 *   POST   /api/transactions?userID=                        → TransactionResponseDTO (201)
 *   PUT    /api/transactions/{id}?userID=                   → TransactionResponseDTO
 *   DELETE /api/transactions/{id}?userID=                   → 204
 *
 * NOTE: SAVING transactions are never added to or subtracted from the running balance.
 *       They are tracked independently for goal/savings progress only.
 */
@RestController
@RequestMapping("/api/transactions")
@CrossOrigin(origins = "*")
public class TransactionController {

    private final TransactionService transactionService;

    @Autowired
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    // ── List (with optional type filter) ─────────────────────────────────────

    /**
     * Returns transactions for a user in a given month/year.
     *
     * When type=RECURRING is passed the service queries transactions whose
     * recurringID is NOT NULL — i.e. they were auto-generated from a
     * RecurringTransaction row.  This is separate from SAVING; a recurring
     * entry can be of type INCOME, EXPENSE, or SAVING.
     */
    @GetMapping
    public ResponseEntity<List<TransactionResponseDTO>> getTransactions(
            @RequestParam Long   userID,
            @RequestParam int    month,
            @RequestParam int    year,
            @RequestParam(required = false) String type) {
        return ResponseEntity.ok(transactionService.getTransactions(userID, month, year, type));
    }

    // ── Single ────────────────────────────────────────────────────────────────

    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponseDTO> getTransaction(
            @PathVariable Long id,
            @RequestParam  Long userID) {
        return ResponseEntity.ok(transactionService.getTransactionByID(id, userID));
    }

    // ── Create ────────────────────────────────────────────────────────────────

    @PostMapping
    public ResponseEntity<TransactionResponseDTO> createTransaction(
            @RequestParam Long userID,
            @RequestBody  TransactionRequestDTO req) {
        TransactionResponseDTO created = transactionService.createTransaction(userID, req);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // ── Update ────────────────────────────────────────────────────────────────

    @PutMapping("/{id}")
    public ResponseEntity<TransactionResponseDTO> updateTransaction(
            @PathVariable Long id,
            @RequestParam  Long userID,
            @RequestBody   TransactionRequestDTO req) {
        return ResponseEntity.ok(transactionService.updateTransaction(id, userID, req));
    }

    // ── Delete ────────────────────────────────────────────────────────────────

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(
            @PathVariable Long id,
            @RequestParam  Long userID) {
        transactionService.deleteTransaction(id, userID);
        return ResponseEntity.noContent().build();
    }
}