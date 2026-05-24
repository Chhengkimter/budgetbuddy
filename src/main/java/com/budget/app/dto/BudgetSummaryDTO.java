package com.budget.app.dto;

import java.math.BigDecimal;
import java.util.List;

/**
 * Full payload returned by GET /api/budgets/summary.
 * Shape matches exactly what budget.js renderDynamicBudgetDashboard() consumes.
 */
public class BudgetSummaryDTO {

    private BigDecimal       totalBudget;
    private BigDecimal       totalSpend;
    private List<BudgetItem> budgetsList;
    private SavingsGoal      savingsGoal;

    // ── Inner: individual budget row ──────────────────────────────────────────

    public static class BudgetItem {
        private Long       budgetID;
        private String     categoryName;   // maps to BudgetName
        private BigDecimal spent;          // computed from transactions
        private BigDecimal limit;          // BudgetLimit
        private String     colorVar;       // CSS variable, cycled from palette

        public Long       getBudgetID()                    { return budgetID; }
        public void       setBudgetID(Long budgetID)       { this.budgetID = budgetID; }

        public String     getCategoryName()                { return categoryName; }
        public void       setCategoryName(String n)        { this.categoryName = n; }

        public BigDecimal getSpent()                       { return spent; }
        public void       setSpent(BigDecimal spent)       { this.spent = spent; }

        public BigDecimal getLimit()                       { return limit; }
        public void       setLimit(BigDecimal limit)       { this.limit = limit; }

        public String     getColorVar()                    { return colorVar; }
        public void       setColorVar(String colorVar)     { this.colorVar = colorVar; }
    }

    // ── Inner: savings goal summary ───────────────────────────────────────────

    public static class SavingsGoal {
        private BigDecimal spent;   // total saving deposits this month
        private BigDecimal target;  // limit of the "Saving" budget

        public BigDecimal getSpent()             { return spent; }
        public void       setSpent(BigDecimal v) { this.spent = v; }

        public BigDecimal getTarget()              { return target; }
        public void       setTarget(BigDecimal v)  { this.target = v; }
    }

    // ── Getters & Setters ─────────────────────────────────────────────────────

    public BigDecimal getTotalBudget()                       { return totalBudget; }
    public void       setTotalBudget(BigDecimal v)           { this.totalBudget = v; }

    public BigDecimal getTotalSpend()                        { return totalSpend; }
    public void       setTotalSpend(BigDecimal v)            { this.totalSpend = v; }

    public List<BudgetItem> getBudgetsList()                 { return budgetsList; }
    public void             setBudgetsList(List<BudgetItem> l) { this.budgetsList = l; }

    public SavingsGoal getSavingsGoal()                      { return savingsGoal; }
    public void        setSavingsGoal(SavingsGoal sg)        { this.savingsGoal = sg; }
}