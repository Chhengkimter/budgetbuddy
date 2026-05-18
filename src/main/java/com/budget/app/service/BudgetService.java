package com.budget.app.service;

import com.budget.app.model.Budget;
import com.budget.app.model.User;
import com.budget.app.repository.BudgetRepository;
import com.budget.app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

// @Service = marks this as a Spring-managed business logic class
// This layer sits between the Controller and the Repository
@Service
public class BudgetService {

    @Autowired
    private BudgetRepository budgetRepository;

    @Autowired
    private UserRepository userRepository;

    // ── Get all budgets for a user ────────────────────
    // Only returns active budgets (is_active = true)
    public List<Budget> getBudgetsByUser(Long userId) {
        return budgetRepository.findByUserIdAndIsActiveTrue(userId);
    }

    // ── Get a single budget by ID ─────────────────────
    public Optional<Budget> getBudgetById(Long id) {
        return budgetRepository.findById(id);
    }

    // ── Create a new budget ───────────────────────────
    // Links the budget to a user and saves it
    public Budget createBudget(Long userId, Budget budget) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        budget.setUser(user);
        return budgetRepository.save(budget);
    }

    // ── Update a budget ───────────────────────────────
    // UPDATED: now also saves category and description (new fields from schema)
    public Budget updateBudget(Long id, Budget updatedBudget) {
        Budget existing = budgetRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Budget not found with id: " + id));

        existing.setName(updatedBudget.getName());
        existing.setTotalAmount(updatedBudget.getTotalAmount());
        existing.setCategory(updatedBudget.getCategory());         // NEW
        existing.setDescription(updatedBudget.getDescription());   // NEW

        return budgetRepository.save(existing);
    }

    // ── Delete a budget ───────────────────────────────
    // Hard delete — also removes all transactions (cascade set in DB)
    public void deleteBudget(Long id) {
        if (!budgetRepository.existsById(id)) {
            throw new RuntimeException("Budget not found with id: " + id);
        }
        budgetRepository.deleteById(id);
    }
}
