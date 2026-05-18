package com.budget.app.repository;

import com.budget.app.model.Budget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

// JpaRepository gives us free CRUD: save(), findById(), findAll(), deleteById(), etc.
// Spring auto-generates SQL based on the method names below
@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long> {

    // Get all active budgets for a user
    // UPDATED: added IsActiveTrue so soft-deleted budgets are excluded
    // SQL: SELECT * FROM budgets WHERE user_id = ? AND is_active = true
    List<Budget> findByUserIdAndIsActiveTrue(Long userId);
}
