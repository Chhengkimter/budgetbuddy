package com.budget.app.repository;

import com.budget.app.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    // Get all transactions for a specific budget
    List<Transaction> findByBudgetId(Long budgetId);

    // Get all transactions for a specific user (across all budgets)
    List<Transaction> findByBudgetUserId(Long userId);
}
