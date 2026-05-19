package com.budget.app.repository;

import com.budget.app.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    
    List<Transaction> findByBudgetIdAndIsDeletedFalse(Long budgetId);

    
    List<Transaction> findByBudgetUserIdAndIsDeletedFalse(Long userId);
}
