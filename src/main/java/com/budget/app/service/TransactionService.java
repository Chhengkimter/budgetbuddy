package com.budget.app.service;

import com.budget.app.dto.TransactionRequestDTO;
import com.budget.app.dto.TransactionResponseDTO;
import com.budget.app.model.Budget;
import com.budget.app.model.Goal;
import com.budget.app.model.Transaction;
import com.budget.app.repository.BudgetRepository;
import com.budget.app.repository.GoalRepository;
import com.budget.app.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    private final TransactionRepository txRepo;
    private final BudgetRepository      budgetRepo;
    private final GoalRepository        goalRepo;

    @Autowired
    public TransactionService(TransactionRepository txRepo,
                              BudgetRepository budgetRepo,
                              GoalRepository goalRepo) {
        this.txRepo     = txRepo;
        this.budgetRepo = budgetRepo;
        this.goalRepo   = goalRepo;
    }

    // ── List ──────────────────────────────────────────────────────────────────

    /**
     * Returns transactions for a user in the given month/year.
     *
     * type values:
     *   null / "ALL"  → every transaction in the month
     *   "INCOME"      → type = INCOME
     *   "EXPENSE"     → type = EXPENSE
     *   "SAVING"      → type = SAVING  (never affects balance)
     *   "RECURRING"   → recurringID IS NOT NULL (any type, auto-generated rows only)
     */
    public List<TransactionResponseDTO> getTransactions(Long userID, int month, int year, String type) {
        List<Transaction> rows;

        if (type == null || type.isBlank() || type.equalsIgnoreCase("ALL")) {
            rows = txRepo.findByUserAndMonth(userID, month, year);
        } else if (type.equalsIgnoreCase("RECURRING")) {
            rows = txRepo.findRecurringByUserAndMonth(userID, month, year);
        } else {
            String normalised = type.toUpperCase();
            validateTransactionType(normalised);
            rows = txRepo.findByUserMonthAndType(userID, month, year, normalised);
        }

        return rows.stream().map(this::toResponseDTO).collect(Collectors.toList());
    }

    // ── Single ────────────────────────────────────────────────────────────────

    public TransactionResponseDTO getTransactionByID(Long id, Long userID) {
        Transaction tx = txRepo.findByTransactionIDAndUserID(id, userID)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Transaction not found"));
        return toResponseDTO(tx);
    }

    // ── Create ────────────────────────────────────────────────────────────────

    /**
     * Creates a manual transaction.
     *
     * SAVING transactions are stored with their full amount but are NEVER
     * included in any balance sum — only in goal/savings progress queries.
     * This is enforced at the repository query level (GoalService, dashboard
     * balance queries) by always filtering WHERE transactionType != 'SAVING'.
     */
    public TransactionResponseDTO createTransaction(Long userID, TransactionRequestDTO req) {
        validateRequest(req);

        Transaction tx = new Transaction(
                userID,
                req.getBudgetID(),
                req.getGoalID(),
                req.getTransactionType().toUpperCase(),
                req.getTransactionDate(),
                req.getTransactionName(),
                req.getTransactionNote(),
                req.getTransactionAmount()
        );

        return toResponseDTO(txRepo.save(tx));
    }

    // ── Update ────────────────────────────────────────────────────────────────

    public TransactionResponseDTO updateTransaction(Long id, Long userID, TransactionRequestDTO req) {
        validateRequest(req);

        Transaction tx = txRepo.findByTransactionIDAndUserID(id, userID)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Transaction not found"));

        tx.setTransactionName(req.getTransactionName());
        tx.setTransactionAmount(req.getTransactionAmount());
        tx.setTransactionDate(req.getTransactionDate());
        tx.setTransactionType(req.getTransactionType().toUpperCase());
        tx.setTransactionNote(req.getTransactionNote());
        tx.setBudgetID(req.getBudgetID());
        tx.setGoalID(req.getGoalID());

        return toResponseDTO(txRepo.save(tx));
    }

    // ── Delete ────────────────────────────────────────────────────────────────

    public void deleteTransaction(Long id, Long userID) {
        Transaction tx = txRepo.findByTransactionIDAndUserID(id, userID)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Transaction not found"));
        txRepo.delete(tx);
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    private TransactionResponseDTO toResponseDTO(Transaction tx) {
        // Resolve display names — safe null-guards for rows with no budget/goal
        String budgetName = null;
        if (tx.getBudgetID() != null) {
            budgetName = budgetRepo.findById(tx.getBudgetID())
                    .map(Budget::getBudgetName)
                    .orElse(null);
        }

        String goalName = null;
        if (tx.getGoalID() != null) {
            goalName = goalRepo.findById(tx.getGoalID())
                    .map(Goal::getGoalName)
                    .orElse(null);
        }

        TransactionResponseDTO dto = new TransactionResponseDTO();
        dto.setTransactionID(tx.getTransactionID());
        dto.setUserID(tx.getUserID());
        dto.setTransactionName(tx.getTransactionName());
        dto.setTransactionAmount(tx.getTransactionAmount());
        dto.setTransactionDate(tx.getTransactionDate());
        dto.setTransactionType(tx.getTransactionType());
        dto.setTransactionNote(tx.getTransactionNote());
        dto.setBudgetID(tx.getBudgetID());
        dto.setBudgetName(budgetName);
        dto.setGoalID(tx.getGoalID());
        dto.setGoalName(goalName);
        dto.setRecurringID(tx.getRecurringID());
        return dto;
    }

    private void validateRequest(TransactionRequestDTO req) {
        if (req.getTransactionName() == null || req.getTransactionName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Transaction name is required");
        }
        if (req.getTransactionAmount() == null || req.getTransactionAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Amount must be positive");
        }
        if (req.getTransactionDate() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Transaction date is required");
        }
        if (req.getTransactionType() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Transaction type is required");
        }
        validateTransactionType(req.getTransactionType().toUpperCase());

        // EXPENSE must link to a budget
        if ("EXPENSE".equals(req.getTransactionType().toUpperCase()) && req.getBudgetID() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "EXPENSE transactions must be linked to a budget");
        }
    }

    private void validateTransactionType(String type) {
        if (!type.equals("INCOME") && !type.equals("EXPENSE") && !type.equals("SAVING")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Invalid transaction type. Accepted: INCOME, EXPENSE, SAVING");
        }
    }
}