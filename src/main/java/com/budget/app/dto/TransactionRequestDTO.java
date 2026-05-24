package com.budget.app.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Inbound payload for POST /api/transactions and PUT /api/transactions/{id}.
 *
 * TransactionType accepted values: INCOME | EXPENSE | SAVING
 *
 * SAVING transactions are linked to a GoalID and a saving Budget.
 * They are NEVER included in balance calculations — only in goal/savings progress.
 */
public class TransactionRequestDTO {

    private String     transactionName;
    private BigDecimal transactionAmount;
    private LocalDate  transactionDate;

    /** INCOME | EXPENSE | SAVING */
    private String     transactionType;

    private String     transactionNote;

    /** Required for EXPENSE transactions. Null for INCOME and SAVING. */
    private Long       budgetID;

    /**
     * Required for SAVING transactions — links the deposit to a specific goal.
     * Null for INCOME and EXPENSE.
     */
    private Long       goalID;

    // ── Getters & Setters ─────────────────────────────────────────────────────

    public String getTransactionName()                         { return transactionName; }
    public void   setTransactionName(String transactionName)   { this.transactionName = transactionName; }

    public BigDecimal getTransactionAmount()                           { return transactionAmount; }
    public void       setTransactionAmount(BigDecimal transactionAmount) { this.transactionAmount = transactionAmount; }

    public LocalDate getTransactionDate()                        { return transactionDate; }
    public void      setTransactionDate(LocalDate transactionDate) { this.transactionDate = transactionDate; }

    public String getTransactionType()                         { return transactionType; }
    public void   setTransactionType(String transactionType)   { this.transactionType = transactionType; }

    public String getTransactionNote()                         { return transactionNote; }
    public void   setTransactionNote(String transactionNote)   { this.transactionNote = transactionNote; }

    public Long getBudgetID()                { return budgetID; }
    public void setBudgetID(Long budgetID)   { this.budgetID = budgetID; }

    public Long getGoalID()              { return goalID; }
    public void setGoalID(Long goalID)   { this.goalID = goalID; }
}