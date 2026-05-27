package com.budget.app.service;

import com.budget.app.dto.BudgetRequestDTO;
import com.budget.app.dto.BudgetSummaryDTO;
import com.budget.app.dto.BudgetSummaryDTO.BudgetItem;
import com.budget.app.dto.BudgetSummaryDTO.SavingsGoal;
import com.budget.app.model.Budget;
import com.budget.app.repository.BudgetRepository;
import com.budget.app.repository.TransactionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BudgetService {

    // Colour palette cycled per budget row (matches CSS variables in budget.css)
    private static final String[] COLOUR_PALETTE = {
        "var(--amber)", "var(--primary)", "var(--green)",
        "var(--blue)",  "var(--red)",     "var(--purple)"
    };

    private final BudgetRepository      budgetRepo;
    private final TransactionRepository txRepo;

    public BudgetService(BudgetRepository budgetRepo, TransactionRepository txRepo) {
        this.budgetRepo = budgetRepo;
        this.txRepo     = txRepo;
    }

    // ── Summary (budget page full payload) ────────────────────────────────────

    public BudgetSummaryDTO getSummary(Long userID, int month, int year) {

        List<Budget> budgets = budgetRepo
                .findByUserIDAndBudgetMonthAndBudgetYear(userID, month, year);

        // Build individual budget rows
        List<BudgetItem> items = buildItems(budgets, month, year);

        // Total budget = sum of all limits
        BigDecimal totalBudget = budgetRepo.sumBudgetLimitByMonthYear(userID, month, year);

        // Total spend = sum of all Spending transactions for the month
        BigDecimal totalSpend = txRepo.sumTotalSpendingByUser(userID, month, year);

        // Savings goal: target = "Saving"-named budget; spent = saving deposits
        BigDecimal savingsTarget    = budgetRepo.sumSavingsBudgetLimit(userID, month, year);
        BigDecimal savingsDeposited = txRepo.sumMonthlySavingsByUser(userID, month, year);

        SavingsGoal savingsGoal = new SavingsGoal();
        savingsGoal.setTarget(savingsTarget);
        savingsGoal.setSpent(savingsDeposited);

        BudgetSummaryDTO dto = new BudgetSummaryDTO();
        dto.setTotalBudget(totalBudget);
        dto.setTotalSpend(totalSpend);
        dto.setBudgetsList(items);
        dto.setSavingsGoal(savingsGoal);
        return dto;
    }

    // ── CRUD ──────────────────────────────────────────────────────────────────

    public Budget createBudget(Long userID, BudgetRequestDTO req) {
        validateRequest(req);
        Budget budget = new Budget(
                userID,
                req.getBudgetName(),
                req.getBudgetLimit(),
                req.getBudgetMonth(),
                req.getBudgetYear(),
                req.isBudgetIsRecurring()
        );
        return budgetRepo.save(budget);
    }

    public Budget updateBudget(Long budgetID, BudgetRequestDTO req) {
        Budget budget = budgetRepo.findById(budgetID)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Budget not found"));

        budget.setBudgetName(req.getBudgetName());
        budget.setBudgetLimit(req.getBudgetLimit());
        budget.setBudgetMonth(req.getBudgetMonth());
        budget.setBudgetYear(req.getBudgetYear());
        budget.setBudgetUpdated(LocalDateTime.now());
        return budgetRepo.save(budget);
    }

    public void deleteBudget(Long budgetID) {
        if (!budgetRepo.existsById(budgetID)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Budget not found");
        }
        budgetRepo.deleteById(budgetID);
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    private List<BudgetItem> buildItems(List<Budget> budgets, int month, int year) {
        final int[] idx = {0};
        return budgets.stream().map(b -> {
            BigDecimal spent = txRepo.sumSpendingByBudget(b.getBudgetID(), month, year);

            BudgetItem item = new BudgetItem();
            item.setBudgetID(b.getBudgetID());
            item.setCategoryName(b.getBudgetName());
            item.setLimit(b.getBudgetLimit());
            item.setSpent(spent);
            item.setColorVar(COLOUR_PALETTE[idx[0]++ % COLOUR_PALETTE.length]);
            return item;
        }).collect(Collectors.toList());
    }

    private void validateRequest(BudgetRequestDTO req) {
        if (req.getBudgetName() == null || req.getBudgetName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Budget name is required");
        }
        if (req.getBudgetLimit() == null || req.getBudgetLimit().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Budget limit must be positive");
        }
        if (req.getBudgetMonth() < 1 || req.getBudgetMonth() > 12) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid month");
        }
    }
}