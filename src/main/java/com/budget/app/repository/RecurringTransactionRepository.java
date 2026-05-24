package com.budget.app.repository;

import com.budget.app.model.RecurringTransaction;
import com.budget.app.model.RecurringTransactionFrequency;
import com.budget.app.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface RecurringTransactionRepository extends JpaRepository<RecurringTransaction, Long> {

    /**
     * Find all recurring transactions for a budget
     * @param budgetId the budget ID
     * @return list of recurring transactions
     */
    List<RecurringTransaction> findByBudgetId(Long budgetId);

    /**
     * Find all active recurring transactions for a budget
     * @param budgetId the budget ID
     * @return list of active recurring transactions
     */
    List<RecurringTransaction> findByBudgetIdAndIsActiveTrue(Long budgetId);

    /**
     * Find all recurring transactions of a specific type for a budget
     * @param budgetId the budget ID
     * @param type the transaction type (INCOME or EXPENSE)
     * @return list of recurring transactions
     */
    List<RecurringTransaction> findByBudgetIdAndType(Long budgetId, Transaction.Type type);

    /**
     * Find all recurring transactions with a specific frequency
     * @param budgetId the budget ID
     * @param frequency the recurrence frequency
     * @return list of recurring transactions
     */
    List<RecurringTransaction> findByBudgetIdAndFrequency(Long budgetId, RecurringTransactionFrequency frequency);

    /**
     * Find all recurring transactions due on a specific date
     * @param budgetId the budget ID
     * @param dueDate the due date
     * @return list of recurring transactions due on the specified date
     */
    List<RecurringTransaction> findByBudgetIdAndNextDueDateAndIsActiveTrue(Long budgetId, LocalDate dueDate);

    /**
     * Find all recurring transactions that are due today or earlier (active and not expired)
     * @param budgetId the budget ID
     * @param today the current date
     * @return list of recurring transactions that need to be processed
     */
    @Query("SELECT rt FROM RecurringTransaction rt " +
           "WHERE rt.budget.id = :budgetId " +
           "AND rt.isActive = true " +
           "AND rt.nextDueDate <= :today " +
           "AND (rt.endDate IS NULL OR rt.endDate >= :today)")
    List<RecurringTransaction> findDueTransactions(@Param("budgetId") Long budgetId, @Param("today") LocalDate today);

    /**
     * Find all recurring transactions for a user (through budget relationship)
     * @param userId the user ID
     * @return list of recurring transactions
     */
    @Query("SELECT rt FROM RecurringTransaction rt " +
           "WHERE rt.budget.user.id = :userId")
    List<RecurringTransaction> findByUserId(@Param("userId") Long userId);

    /**
     * Find all active recurring transactions for a user due by a certain date
     * @param userId the user ID
     * @param beforeDate the cutoff date
     * @return list of recurring transactions
     */
    @Query("SELECT rt FROM RecurringTransaction rt " +
           "WHERE rt.budget.user.id = :userId " +
           "AND rt.isActive = true " +
           "AND rt.nextDueDate <= :beforeDate " +
           "AND (rt.endDate IS NULL OR rt.endDate >= :beforeDate)")
    List<RecurringTransaction> findDueByUser(@Param("userId") Long userId, @Param("beforeDate") LocalDate beforeDate);

    /**
     * Count active recurring transactions for a budget
     * @param budgetId the budget ID
     * @return count of active recurring transactions
     */
    Long countByBudgetIdAndIsActiveTrue(Long budgetId);

    /**
     * Check if a recurring transaction exists for a user
     * @param id the recurring transaction ID
     * @param userId the user ID
     * @return true if exists and belongs to user, false otherwise
     */
    @Query("SELECT CASE WHEN COUNT(rt) > 0 THEN true ELSE false END " +
           "FROM RecurringTransaction rt " +
           "WHERE rt.id = :id AND rt.budget.user.id = :userId")
    boolean existsByIdAndUserId(@Param("id") Long id, @Param("userId") Long userId);

    /**
     * Find a recurring transaction by ID and user ID
     * @param id the recurring transaction ID
     * @param userId the user ID
     * @return Optional containing the recurring transaction if found
     */
    @Query("SELECT rt FROM RecurringTransaction rt " +
           "WHERE rt.id = :id AND rt.budget.user.id = :userId")
    Optional<RecurringTransaction> findByIdAndUserId(@Param("id") Long id, @Param("userId") Long userId);
}
