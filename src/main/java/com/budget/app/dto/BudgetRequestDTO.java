package com.budget.app.dto;

import java.math.BigDecimal;

/**
 * Payload accepted by POST /api/budgets and PUT /api/budgets/{id}.
 */
public class BudgetRequestDTO {

    private String     budgetName;
    private BigDecimal budgetLimit;
    private int        budgetMonth;
    private int        budgetYear;
    private boolean    budgetIsRecurring;

    // ── Getters & Setters ─────────────────────────────────────────────────────

    public String getBudgetName()                        { return budgetName; }
    public void   setBudgetName(String budgetName)       { this.budgetName = budgetName; }

    public BigDecimal getBudgetLimit()                   { return budgetLimit; }
    public void       setBudgetLimit(BigDecimal limit)   { this.budgetLimit = limit; }

    public int  getBudgetMonth()                         { return budgetMonth; }
    public void setBudgetMonth(int budgetMonth)          { this.budgetMonth = budgetMonth; }

    public int  getBudgetYear()                          { return budgetYear; }
    public void setBudgetYear(int budgetYear)            { this.budgetYear = budgetYear; }

    public boolean isBudgetIsRecurring()                 { return budgetIsRecurring; }
    public void    setBudgetIsRecurring(boolean v)       { this.budgetIsRecurring = v; }
}