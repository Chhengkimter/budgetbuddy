package com.budget.app.controller;

import com.budget.app.dto.GoalOverviewDTO;
import com.budget.app.dto.GoalRequestDTO;
import com.budget.app.dto.GoalResponseDTO;
import com.budget.app.service.GoalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for saving goals.
 *
 * Endpoints:
 *   GET    /api/goals/overview?userID=&month=&year=   → GoalOverviewDTO (goals page)
 *   GET    /api/goals?userID=                         → List<GoalResponseDTO>
 *   GET    /api/goals/{goalID}?userID=                → GoalResponseDTO
 *   POST   /api/goals?userID=                         → GoalResponseDTO (201)
 *   PUT    /api/goals/{goalID}?userID=                → GoalResponseDTO
 *   PATCH  /api/goals/{goalID}/complete?userID=       → GoalResponseDTO (mark finished)
 *   DELETE /api/goals/{goalID}?userID=                → 204
 */
@RestController
@RequestMapping("/api/goals")
@CrossOrigin(origins = "*")
public class GoalController {

    private final GoalService goalService;

    @Autowired
    public GoalController(GoalService goalService) {
        this.goalService = goalService;
    }

    // ── Full page overview (used by goals.html) ───────────────────────────────

    @GetMapping("/overview")
    public ResponseEntity<GoalOverviewDTO> getOverview(
            @RequestParam Long userID,
            @RequestParam int  month,
            @RequestParam int  year) {
        return ResponseEntity.ok(goalService.getOverview(userID, month, year));
    }

    // ── List & single ─────────────────────────────────────────────────────────

    @GetMapping
    public ResponseEntity<List<GoalResponseDTO>> getAllGoals(@RequestParam Long userID) {
        return ResponseEntity.ok(goalService.getAllGoals(userID));
    }

    @GetMapping("/{goalID}")
    public ResponseEntity<GoalResponseDTO> getGoal(
            @PathVariable Long goalID,
            @RequestParam  Long userID) {
        return ResponseEntity.ok(goalService.getGoalByID(goalID, userID));
    }

    // ── Create ────────────────────────────────────────────────────────────────

    @PostMapping
    public ResponseEntity<GoalResponseDTO> createGoal(
            @RequestParam Long userID,
            @RequestBody  GoalRequestDTO req) {
        GoalResponseDTO created = goalService.createGoal(userID, req);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // ── Update ────────────────────────────────────────────────────────────────

    @PutMapping("/{goalID}")
    public ResponseEntity<GoalResponseDTO> updateGoal(
            @PathVariable Long goalID,
            @RequestParam  Long userID,
            @RequestBody   GoalRequestDTO req) {
        return ResponseEntity.ok(goalService.updateGoal(goalID, userID, req));
    }

    // ── Mark as complete ──────────────────────────────────────────────────────

    @PatchMapping("/{goalID}/complete")
    public ResponseEntity<GoalResponseDTO> completeGoal(
            @PathVariable Long goalID,
            @RequestParam  Long userID) {
        return ResponseEntity.ok(goalService.completeGoal(goalID, userID));
    }

    // ── Delete ────────────────────────────────────────────────────────────────

    @DeleteMapping("/{goalID}")
    public ResponseEntity<Void> deleteGoal(
            @PathVariable Long goalID,
            @RequestParam  Long userID) {
        goalService.deleteGoal(goalID, userID);
        return ResponseEntity.noContent().build();
    }
}