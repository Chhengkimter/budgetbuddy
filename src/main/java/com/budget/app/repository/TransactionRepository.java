package com.budget.app.repository;

import com.budget.app.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    // Get all non-deleted transactions for a specific budget
    // UPDATED: added IsDeletedFalse to exclude soft-deleted transactions
    // SQL: SELECT * FROM transactions WHERE budget_id = ? AND is_deleted = false
    List<Transaction> findByBudgetIdAndIsDeletedFalse(Long budgetId);

    // Get all non-deleted transactions across all budgets for a user
    // SQL: SELECT * FROM transactions WHERE budget.user_id = ? AND is_deleted = false
    List<Transaction> findByBudgetUserIdAndIsDeletedFalse(Long userId);
}
