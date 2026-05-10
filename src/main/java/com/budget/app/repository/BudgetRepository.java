package com.budget.app.repository;

import com.budget.app.model.Budget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long> {

    // Get all budgets for a specific user
    List<Budget> findByUserId(Long userId);
}
