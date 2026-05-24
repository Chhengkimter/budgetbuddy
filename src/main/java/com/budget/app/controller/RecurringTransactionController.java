package com.budget.app.controller;

import com.budget.app.dto.RecurringTransactionRequestDTO;
import com.budget.app.dto.RecurringTransactionResponseDTO;
import com.budget.app.service.RecurringTransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for recurring transaction templates.
 *
 * A RecurringTransaction is a *template* row — it is NOT itself a balance entry.
 * The scheduler reads active templates and auto-generates real Transaction rows
 * each month on the configured RecurringDay.
 *
 * The "Monthly Saving Goal" feature on the Goals page is one such template:
 *   RTransactionType = SAVING, linked to a Budget (monthly saving budget) and
 *   optionally to a Goal.  The generated Transaction rows count toward goal
 *   progress but are NEVER added to the user's running balance.
 *
 * Endpoints:
 *   GET    /api/recurring?userID=              → List<RecurringTransactionResponseDTO>
 *   GET    /api/recurring/{id}?userID=         → RecurringTransactionResponseDTO
 *   POST   /api/recurring?userID=             → RecurringTransactionResponseDTO (201)
 *   PUT    /api/recurring/{id}?userID=         → RecurringTransactionResponseDTO
 *   PATCH  /api/recurring/{id}/deactivate?userID= → RecurringTransactionResponseDTO
 *   DELETE /api/recurring/{id}?userID=         → 204
 */
@RestController
@RequestMapping("/api/recurring")
@CrossOrigin(origins = "*")
public class RecurringTransactionController {

    private final RecurringTransactionService recurringService;

    @Autowired
    public RecurringTransactionController(RecurringTransactionService recurringService) {
        this.recurringService = recurringService;
    }

    // ── List ──────────────────────────────────────────────────────────────────

    @GetMapping
    public ResponseEntity<List<RecurringTransactionResponseDTO>> getAll(
            @RequestParam Long userID) {
        return ResponseEntity.ok(recurringService.getAllByUser(userID));
    }

    // ── Single ────────────────────────────────────────────────────────────────

    @GetMapping("/{id}")
    public ResponseEntity<RecurringTransactionResponseDTO> getOne(
            @PathVariable Long id,
            @RequestParam  Long userID) {
        return ResponseEntity.ok(recurringService.getByID(id, userID));
    }

    @PostMapping
    public ResponseEntity<RecurringTransactionResponseDTO> create(
            @RequestParam Long userID,
            @RequestBody  RecurringTransactionRequestDTO req) {
        RecurringTransactionResponseDTO created = recurringService.create(userID, req);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // ── Update ────────────────────────────────────────────────────────────────

    @PutMapping("/{id}")
    public ResponseEntity<RecurringTransactionResponseDTO> update(
            @PathVariable Long id,
            @RequestParam  Long userID,
            @RequestBody   RecurringTransactionRequestDTO req) {
        return ResponseEntity.ok(recurringService.update(id, userID, req));
    }

    // ── Soft-deactivate (stop without deleting history) ───────────────────────

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<RecurringTransactionResponseDTO> deactivate(
            @PathVariable Long id,
            @RequestParam  Long userID) {
        return ResponseEntity.ok(recurringService.deactivate(id, userID));
    }

    // ── Hard delete ───────────────────────────────────────────────────────────

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            @RequestParam  Long userID) {
        recurringService.delete(id, userID);
        return ResponseEntity.noContent().build();
    }
}