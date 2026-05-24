package com.budget.app.repository;

import com.budget.app.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    // ── Used by TransactionService ────────────────────────────────────────────

    /** All transactions for a user in a given month/year, newest first. */
    @Query("SELECT t FROM Transaction t " +
           "WHERE t.userID = :userID " +
           "AND MONTH(t.transactionDate) = :month " +
           "AND YEAR(t.transactionDate)  = :year " +
           "ORDER BY t.transactionDate DESC")
    List<Transaction> findByUserAndMonth(@Param("userID") Long userID,
                                         @Param("month")  int  month,
                                         @Param("year")   int  year);

    /** Transactions filtered by type (INCOME | EXPENSE | SAVING) for a month/year. */
    @Query("SELECT t FROM Transaction t " +
           "WHERE t.userID = :userID " +
           "AND t.transactionType = :type " +
           "AND MONTH(t.transactionDate) = :month " +
           "AND YEAR(t.transactionDate)  = :year " +
           "ORDER BY t.transactionDate DESC")
    List<Transaction> findByUserMonthAndType(@Param("userID") Long   userID,
                                              @Param("month")  int    month,
                                              @Param("year")   int    year,
                                              @Param("type")   String type);

    /**
     * Transactions that were auto-generated from a RecurringTransaction template
     * (recurringID IS NOT NULL) for a given month/year.
     * Used by the RECURRING filter on the transactions page.
     */
    @Query("SELECT t FROM Transaction t " +
           "WHERE t.userID = :userID " +
           "AND t.recurringID IS NOT NULL " +
           "AND MONTH(t.transactionDate) = :month " +
           "AND YEAR(t.transactionDate)  = :year " +
           "ORDER BY t.transactionDate DESC")
    List<Transaction> findRecurringByUserAndMonth(@Param("userID") Long userID,
                                                   @Param("month")  int  month,
                                                   @Param("year")   int  year);

    /** Ownership check — prevents cross-user access. */
    Optional<Transaction> findByTransactionIDAndUserID(Long transactionID, Long userID);

    // ── Used by GoalService ───────────────────────────────────────────────────

    /**
     * Total saved across ALL goals for a user (all-time).
     * Only sums SAVING transactions — never INCOME or EXPENSE.
     */
    @Query("SELECT COALESCE(SUM(t.transactionAmount), 0) FROM Transaction t " +
           "WHERE t.userID = :userID AND t.transactionType = 'SAVING'")
    BigDecimal sumAllSavingsByUser(@Param("userID") Long userID);

    /**
     * Total SAVING transactions deposited in a specific month/year.
     * Used for the Monthly Saving Goal progress bar on the Goals page.
     */
    @Query("SELECT COALESCE(SUM(t.transactionAmount), 0) FROM Transaction t " +
           "WHERE t.userID = :userID " +
           "AND t.transactionType = 'SAVING' " +
           "AND MONTH(t.transactionDate) = :month " +
           "AND YEAR(t.transactionDate)  = :year")
    BigDecimal sumMonthlySavingsByUser(@Param("userID") Long userID,
                                       @Param("month")  int  month,
                                       @Param("year")   int  year);

    /**
     * Total saved toward a specific goal (all-time).
     * Used to calculate per-goal progress percent on the Goals page.
     */
    @Query("SELECT COALESCE(SUM(t.transactionAmount), 0) FROM Transaction t " +
           "WHERE t.goalID = :goalID AND t.transactionType = 'SAVING'")
    BigDecimal sumSavedByGoal(@Param("goalID") Long goalID);

    @Query("SELECT COALESCE(SUM(t.transactionAmount), 0) FROM Transaction t " +
       "WHERE t.userID = :userID AND t.transactionType = 'EXPENSE' " +
       "AND MONTH(t.transactionDate) = :month AND YEAR(t.transactionDate) = :year")
    BigDecimal sumTotalSpendingByUser(@Param("userID") Long userID,
                                    @Param("month")  int  month,
                                    @Param("year")   int  year);

    @Query("SELECT COALESCE(SUM(t.transactionAmount), 0) FROM Transaction t " +
        "WHERE t.budgetID = :budgetID AND t.transactionType = 'EXPENSE' " +
        "AND MONTH(t.transactionDate) = :month AND YEAR(t.transactionDate) = :year")
    BigDecimal sumSpendingByBudget(@Param("budgetID") Long budgetID,
                                @Param("month")    int  month,
                                @Param("year")     int  year);
}