package com.budget.app.service;

import com.budget.app.model.Budget;
import com.budget.app.model.RecurringTransaction;
import com.budget.app.model.RecurringTransactionFrequency;
import com.budget.app.model.Transaction;
import com.budget.app.repository.BudgetRepository;
import com.budget.app.repository.RecurringTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class RecurringTransactionService {

    @Autowired
    private RecurringTransactionRepository recurringTransactionRepository;

    @Autowired
    private BudgetRepository budgetRepository;

    // ── Get all recurring transactions for a budget ───
    public List<RecurringTransaction> getRecurringTransactionsByBudget(Long budgetId) {
        return recurringTransactionRepository.findByBudgetId(budgetId);
    }

    // ── Get active recurring transactions for a budget ─
    public List<RecurringTransaction> getActiveRecurringTransactionsByBudget(Long budgetId) {
        return recurringTransactionRepository.findByBudgetIdAndIsActiveTrue(budgetId);
    }

    // ── Get a single recurring transaction by ID ──────
    public Optional<RecurringTransaction> getRecurringTransactionById(Long id) {
        return recurringTransactionRepository.findById(id);
    }

    // ── Get all due (overdue) recurring transactions ──
    public List<RecurringTransaction> getDueRecurringTransactions(Long budgetId) {
        return recurringTransactionRepository.findDueTransactions(budgetId, LocalDate.now());
    }

    // ── Get all due recurring transactions for a user ─
    public List<RecurringTransaction> getDueRecurringTransactionsByUser(Long userId) {
        return recurringTransactionRepository.findDueByUser(userId, LocalDate.now());
    }

    // ── Get by type (INCOME/EXPENSE) ────────────────
    public List<RecurringTransaction> getRecurringTransactionsByType(Long budgetId, Transaction.Type type) {
        return recurringTransactionRepository.findByBudgetIdAndType(budgetId, type);
    }

    // ── Get by frequency ─────────────────────────────
    public List<RecurringTransaction> getRecurringTransactionsByFrequency(Long budgetId, RecurringTransactionFrequency frequency) {
        return recurringTransactionRepository.findByBudgetIdAndFrequency(budgetId, frequency);
    }

    // ── Get all for a user ───────────────────────────
    public List<RecurringTransaction> getRecurringTransactionsByUser(Long userId) {
        return recurringTransactionRepository.findByUserId(userId);
    }

    // ── Create a new recurring transaction ──────────
    public RecurringTransaction createRecurringTransaction(Long budgetId, RecurringTransaction transaction) {
        Budget budget = budgetRepository.findById(budgetId)
            .orElseThrow(() -> new RuntimeException("Budget not found with id: " + budgetId));

        transaction.setBudget(budget);
        transaction.setCreatedAt(LocalDateTime.now());
        transaction.setUpdatedAt(LocalDateTime.now());
        transaction.setIsActive(true);

        return recurringTransactionRepository.save(transaction);
    }

    // ── Update a recurring transaction ──────────────
    public RecurringTransaction updateRecurringTransaction(Long id, RecurringTransaction updatedTransaction) {
        RecurringTransaction existing = recurringTransactionRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Recurring transaction not found with id: " + id));

        existing.setDescription(updatedTransaction.getDescription());
        existing.setAmount(updatedTransaction.getAmount());
        existing.setType(updatedTransaction.getType());
        existing.setCategoryTag(updatedTransaction.getCategoryTag());
        existing.setFrequency(updatedTransaction.getFrequency());
        existing.setNextDueDate(updatedTransaction.getNextDueDate());
        existing.setEndDate(updatedTransaction.getEndDate());
        existing.setUpdatedAt(LocalDateTime.now());

        return recurringTransactionRepository.save(existing);
    }

    // ── Toggle active status ────────────────────────
    public RecurringTransaction toggleActive(Long id) {
        RecurringTransaction existing = recurringTransactionRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Recurring transaction not found with id: " + id));

        existing.setIsActive(!existing.getIsActive());
        existing.setUpdatedAt(LocalDateTime.now());

        return recurringTransactionRepository.save(existing);
    }

    // ── Activate a recurring transaction ───────────
    public RecurringTransaction activate(Long id) {
        RecurringTransaction existing = recurringTransactionRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Recurring transaction not found with id: " + id));

        if (!existing.getIsActive()) {
            existing.setIsActive(true);
            existing.setUpdatedAt(LocalDateTime.now());
            return recurringTransactionRepository.save(existing);
        }
        return existing;
    }

    // ── Deactivate a recurring transaction ────────
    public RecurringTransaction deactivate(Long id) {
        RecurringTransaction existing = recurringTransactionRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Recurring transaction not found with id: " + id));

        if (existing.getIsActive()) {
            existing.setIsActive(false);
            existing.setUpdatedAt(LocalDateTime.now());
            return recurringTransactionRepository.save(existing);
        }
        return existing;
    }

    // ── Advance to next occurrence ──────────────────
    public RecurringTransaction advanceToNextOccurrence(Long id) {
        RecurringTransaction existing = recurringTransactionRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Recurring transaction not found with id: " + id));

        existing.advanceToNextOccurrence();
        return recurringTransactionRepository.save(existing);
    }

    // ── Check if due ────────────────────────────────
    public boolean isRecurringTransactionDue(Long id) {
        return recurringTransactionRepository.findById(id)
            .map(rt -> rt.isDue() && rt.isStillActive())
            .orElse(false);
    }

    // ── Check if active ─────────────────────────────
    public boolean isRecurringTransactionActive(Long id) {
        return recurringTransactionRepository.findById(id)
            .map(RecurringTransaction::isStillActive)
            .orElse(false);
    }

    // ── Count active recurring transactions ────────
    public Long countActiveRecurringTransactions(Long budgetId) {
        return recurringTransactionRepository.countByBudgetIdAndIsActiveTrue(budgetId);
    }

    // ── Delete a recurring transaction ──────────────
    public void deleteRecurringTransaction(Long id) {
        if (!recurringTransactionRepository.existsById(id)) {
            throw new RuntimeException("Recurring transaction not found with id: " + id);
        }
        recurringTransactionRepository.deleteById(id);
    }

    // ── Check if user owns the recurring transaction ─
    public boolean isUserOwner(Long id, Long userId) {
        return recurringTransactionRepository.existsByIdAndUserId(id, userId);
    }

    // ── Get recurring transaction by ID and user ID ──
    public Optional<RecurringTransaction> getRecurringTransactionByIdAndUser(Long id, Long userId) {
        return recurringTransactionRepository.findByIdAndUserId(id, userId);
    }
}
