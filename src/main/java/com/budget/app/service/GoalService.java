package com.budget.app.service;

import com.budget.app.dto.GoalCompleteResponseDTO;
import com.budget.app.dto.GoalOverviewDTO;
import com.budget.app.dto.GoalRequestDTO;
import com.budget.app.dto.GoalResponseDTO;
import com.budget.app.model.Goal;
import com.budget.app.model.Transaction;
import com.budget.app.repository.BudgetRepository;
import com.budget.app.repository.GoalRepository;
import com.budget.app.repository.TransactionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GoalService {

    private final GoalRepository        goalRepo;
    private final TransactionRepository txRepo;
    private final BudgetRepository      budgetRepo;

    public GoalService(GoalRepository goalRepo,
                       TransactionRepository txRepo,
                       BudgetRepository budgetRepo) {
        this.goalRepo   = goalRepo;
        this.txRepo     = txRepo;
        this.budgetRepo = budgetRepo;
    }

    // ── Overview (goals page full payload) ────────────────────────────────────

    public GoalOverviewDTO getOverview(Long userID, int month, int year) {

        // 1. Total saved all-time across all goals
        BigDecimal totalSaved = txRepo.sumAllSavingsByUser(userID);

        // 2. Monthly saving goal target — derived from any budget named "Saving"
        BigDecimal monthlyTarget = budgetRepo.sumSavingsBudgetLimit(userID, month, year);

        // 3. Monthly deposited — sum of Saving transactions this month
        BigDecimal monthlyDeposited = txRepo.sumMonthlySavingsByUser(userID, month, year);

        // 4. Monthly progress percent
        int monthlyPct = 0;
        if (monthlyTarget.compareTo(BigDecimal.ZERO) > 0) {
            monthlyPct = monthlyDeposited
                    .multiply(BigDecimal.valueOf(100))
                    .divide(monthlyTarget, 0, RoundingMode.HALF_UP)
                    .min(BigDecimal.valueOf(100))
                    .intValue();
        }

        // 5. Goal cards
        List<GoalResponseDTO> goalsList = goalRepo
                .findActiveGoalsByUser(userID)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());

        GoalOverviewDTO dto = new GoalOverviewDTO();
        dto.setTotalSaved(totalSaved);
        dto.setMonthlyTarget(monthlyTarget);
        dto.setMonthlyDeposited(monthlyDeposited);
        dto.setMonthlyProgressPercent(monthlyPct);
        dto.setGoalsList(goalsList);
        return dto;
    }

    // ── CRUD ──────────────────────────────────────────────────────────────────

    public List<GoalResponseDTO> getAllGoals(Long userID) {
        return goalRepo.findByUserIDOrderByGoalCreatedDateDesc(userID)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    public GoalResponseDTO getGoalByID(Long goalID, Long userID) {
        Goal goal = goalRepo.findByGoalIDAndUserID(goalID, userID)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Goal not found"));
        return toResponseDTO(goal);
    }

    public GoalResponseDTO createGoal(Long userID, GoalRequestDTO req) {
        validateRequest(req);
        Goal goal = new Goal(userID, req.getGoalName(), req.getGoalAmount(), req.getGoalTargetDate());
        goal.setMonthlyTarget(req.getMonthlyTarget());
        goal.setAutoMonthly(req.getAutoMonthly() != null ? req.getAutoMonthly() : false);
        goal.setIsCompleted(false);
        goal.setCurrentSaved(BigDecimal.ZERO);
        return toResponseDTO(goalRepo.save(goal));
    }

    public GoalResponseDTO updateGoal(Long goalID, Long userID, GoalRequestDTO req) {
        validateRequest(req);
        Goal goal = goalRepo.findByGoalIDAndUserID(goalID, userID)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Goal not found"));

        goal.setGoalName(req.getGoalName());
        goal.setGoalAmount(req.getGoalAmount());
        goal.setGoalTargetDate(req.getGoalTargetDate());
        goal.setMonthlyTarget(req.getMonthlyTarget());
        goal.setAutoMonthly(req.getAutoMonthly() != null ? req.getAutoMonthly() : false);
        return toResponseDTO(goalRepo.save(goal));
    }

    /** Marks a goal as finished (soft-complete) and creates a transfer transaction for remaining amount. */
    public GoalCompleteResponseDTO completeGoal(Long goalID, Long userID) {
        Goal goal = goalRepo.findByGoalIDAndUserID(goalID, userID)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Goal not found"));

        goal.setIsCompleted(true);
        goal.setGoalFinishedDate(LocalDate.now());

        // Calculate remaining amount not yet saved
        BigDecimal currentSaved = txRepo.sumSavedByGoal(goalID);
        BigDecimal remaining = goal.getGoalAmount().subtract(currentSaved);
        BigDecimal transferredAmount = BigDecimal.ZERO;

        // If there's a remaining amount, create a completion transfer transaction
        if (remaining.compareTo(BigDecimal.ZERO) > 0) {
            Transaction completionTx = new Transaction(
                    userID,
                    null,  // no budget link
                    goalID,
                    "SAVING",
                    LocalDate.now(),
                    "Goal completion transfer",
                    null,
                    remaining
            );
            txRepo.save(completionTx);
            transferredAmount = remaining;
        }

        Goal savedGoal = goalRepo.save(goal);
        String message = transferredAmount.compareTo(BigDecimal.ZERO) > 0
                ? "Goal completed. Transfer of " + transferredAmount + " created."
                : "Goal completed. No additional transfer needed.";

        GoalCompleteResponseDTO response = new GoalCompleteResponseDTO();
        response.setGoal(toResponseDTO(savedGoal));
        response.setTransferredAmount(transferredAmount);
        response.setMessage(message);
        return response;
    }

    /** Deposits an amount to a goal as a saving transaction. */
    public GoalResponseDTO depositToGoal(Long goalID, Long userID, BigDecimal amount, LocalDate depositDate) {
        Goal goal = goalRepo.findByGoalIDAndUserID(goalID, userID)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Goal not found"));

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Deposit amount must be positive");
        }

        // Create a saving transaction for this deposit
        Transaction deposit = new Transaction(
                userID,
                null,  // no budget link
                goalID,
                "SAVING",
                depositDate != null ? depositDate : LocalDate.now(),
                "Goal deposit",
                null,
                amount
        );
        txRepo.save(deposit);

        // Update goal's currentSaved field
        BigDecimal newSaved = txRepo.sumSavedByGoal(goalID);
        goal.setCurrentSaved(newSaved);
        goalRepo.save(goal);

        return toResponseDTO(goal);
    }

    public void deleteGoal(Long goalID, Long userID) {
        Goal goal = goalRepo.findByGoalIDAndUserID(goalID, userID)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Goal not found"));
        goalRepo.delete(goal);
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    private GoalResponseDTO toResponseDTO(Goal goal) {
        BigDecimal saved = txRepo.sumSavedByGoal(goal.getGoalID());

        double pct = 0.0;
        if (goal.getGoalAmount() != null && goal.getGoalAmount().compareTo(BigDecimal.ZERO) > 0) {
            pct = saved
                    .multiply(BigDecimal.valueOf(100))
                    .divide(goal.getGoalAmount(), 2, RoundingMode.HALF_UP)
                    .min(BigDecimal.valueOf(100))
                    .doubleValue();
        }

        BigDecimal remaining = null;
        if (goal.getGoalAmount() != null) {
            remaining = goal.getGoalAmount().subtract(saved);
        }

        GoalResponseDTO dto = new GoalResponseDTO();
        dto.setGoalID(goal.getGoalID());
        dto.setUserID(goal.getUserID());
        dto.setGoalName(goal.getGoalName());
        dto.setGoalAmount(goal.getGoalAmount());
        dto.setCurrentSaved(saved);
        dto.setMonthlyTarget(goal.getMonthlyTarget());
        dto.setAutoMonthly(goal.getAutoMonthly());
        dto.setIsCompleted(goal.getIsCompleted());
        dto.setGoalCreatedDate(goal.getGoalCreatedDate());
        dto.setGoalTargetDate(goal.getGoalTargetDate());
        dto.setGoalFinishedDate(goal.getGoalFinishedDate());
        dto.setProgressPercent(pct);
        dto.setRemainingAmount(remaining);
        return dto;
    }

    private void validateRequest(GoalRequestDTO req) {
        if (req.getGoalName() == null || req.getGoalName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Goal name is required");
        }
        if (req.getGoalAmount() == null || req.getGoalAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Goal amount must be positive");
        }
    }
}