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
    
    public List<Budget> getBudgetsByUser(Long userId) {
        return budgetRepository.findByUserIdAndIsActiveTrue(userId);
    }
    public Optional<Budget> getBudgetById(Long id) {
        return budgetRepository.findById(id);
    }
    public Budget createBudget(Long userId, Budget budget) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        budget.setUser(user);
        return budgetRepository.save(budget);
    }
    public Budget updateBudget(Long id, Budget updatedBudget) {
        Budget existing = budgetRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Budget not found with id: " + id));

        existing.setName(updatedBudget.getName());
        existing.setTotalAmount(updatedBudget.getTotalAmount());
        existing.setCategory(updatedBudget.getCategory());
        existing.setDescription(updatedBudget.getDescription());

        return budgetRepository.save(existing);
    }
    public void deleteBudget(Long id) {
        if (!budgetRepository.existsById(id)) {
            throw new RuntimeException("Budget not found with id: " + id);
        }
        budgetRepository.deleteById(id);
    }
}
