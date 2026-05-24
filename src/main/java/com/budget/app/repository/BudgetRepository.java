package com.budget.app.repository;

import com.budget.app.model.Budget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long> {

    /** All budgets for a user in a given month/year. */
    List<Budget> findByUserIDAndBudgetMonthAndBudgetYear(Long userID, int month, int year);

    /** Single budget owned by a specific user (prevents cross-user access). */
    Optional<Budget> findByBudgetIDAndUserID(Long budgetID, Long userID);

    /** Sum of all budget limits for a user in a month/year (= total budget). */
    @Query("SELECT COALESCE(SUM(b.budgetLimit), 0) FROM Budget b " +
           "WHERE b.userID = :userID AND b.budgetMonth = :month AND b.budgetYear = :year")
    BigDecimal sumBudgetLimitByMonthYear(@Param("userID") Long userID,
                                         @Param("month")  int month,
                                         @Param("year")   int year);

    /**
     * Sum of limits for budgets whose name contains "saving" (case-insensitive).
     * Used to derive the monthly savings goal target on the budget summary card.
     */
    @Query("SELECT COALESCE(SUM(b.budgetLimit), 0) FROM Budget b " +
           "WHERE b.userID = :userID AND b.budgetMonth = :month AND b.budgetYear = :year " +
           "AND LOWER(b.budgetName) LIKE '%saving%'")
    BigDecimal sumSavingsBudgetLimit(@Param("userID") Long userID,
                                     @Param("month")  int month,
                                     @Param("year")   int year);
}