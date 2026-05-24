package com.budget.app.service;

import com.budget.app.model.Budget;
import com.budget.app.model.Transaction;
import com.budget.app.repository.BudgetRepository;
import com.budget.app.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private BudgetRepository budgetRepository;
    
    public List<Transaction> getTransactionsByBudget(Long budgetId) {
        return transactionRepository.findByBudgetIdAndIsDeletedFalse(budgetId);
    }
    
    public List<Transaction> getTransactionsByUser(Long userId) {
        return transactionRepository.findByBudgetUserIdAndIsDeletedFalse(userId);
    }
    public Optional<Transaction> getTransactionById(Long id) {
        return transactionRepository.findById(id);
    }
    
    public Transaction addTransaction(Long budgetId, Transaction transaction) {
        Budget budget = budgetRepository.findById(budgetId)
            .orElseThrow(() -> new RuntimeException("Budget not found with id: " + budgetId));
        transaction.setBudget(budget);
        return transactionRepository.save(transaction);
    }
    
    public void deleteTransaction(Long id) {
        Transaction transaction = transactionRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Transaction not found with id: " + id));
        transaction.setIsDeleted(true);
        transactionRepository.save(transaction);
    }
}
