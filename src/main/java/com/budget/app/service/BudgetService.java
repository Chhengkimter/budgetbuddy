package com.budget.app.service;

import com.budget.app.model.Budget;
import com.budget.app.model.User;
import com.budget.app.repository.BudgetRepository;
import com.budget.app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class BudgetService {

    @Autowired
    private BudgetRepository budgetRepository;

    @Autowired
    private UserRepository userRepository;

    // ── Get all budgets for a user ────────────────────
    public List<Budget> getBudgetsByUser(Long userId) {
        return budgetRepository.findByUserId(userId);
    }

    // ── Get a single budget by ID ─────────────────────
    public Optional<Budget> getBudgetById(Long id) {
        return budgetRepository.findById(id);
    }

    // ── Create a new budget ───────────────────────────
    public Budget createBudget(Long userId, Budget budget) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        budget.setUser(user);
        return budgetRepository.save(budget);
    }

    // ── Update a budget ───────────────────────────────
    public Budget updateBudget(Long id, Budget updatedBudget) {
        Budget existing = budgetRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Budget not found with id: " + id));
        existing.setName(updatedBudget.getName());
        existing.setTotalAmount(updatedBudget.getTotalAmount());
        return budgetRepository.save(existing);
    }

    // ── Delete a budget ───────────────────────────────
    public void deleteBudget(Long id) {
        if (!budgetRepository.existsById(id)) {
            throw new RuntimeException("Budget not found with id: " + id);
        }
        budgetRepository.deleteById(id);
    }
}
