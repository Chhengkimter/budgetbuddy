package com.budget.app.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Inbound payload for POST /api/recurring and PUT /api/recurring/{id}.
 *
 * A RecurringTransaction is a *template* — it is not a balance entry itself.
 * The scheduler reads active templates and generates real Transaction rows
 * on recurringDay each month between rtStartDate and rtEndDate.
 *
 * RTransactionType accepted values: INCOME | EXPENSE | SAVING
 *
 * When type is SAVING the generated transactions are linked to goalID and
 * counted toward savings progress only — never toward the running balance.
 */
public class RecurringTransactionRequestDTO {

    /** Optional — links generated EXPENSE transactions to a budget category. */
    private Long budgetID;

    /**
     * Optional — links generated SAVING transactions to a specific goal.
     * Should be null for INCOME and EXPENSE types.
     */
    private Long goalID;

    /** INCOME | EXPENSE | SAVING */
    private String     rTransactionType;
    private String     rTransactionName;
    private String     rTransactionNote;
    private BigDecimal rTransactionAmount;

    /**
     * Day of month (1–28) on which the scheduler fires.
     * Capped at 28 to avoid issues with February.
     */
    private int recurringDay;

    /** First date from which the template is eligible to generate transactions. */
    private LocalDate rtStartDate;

    /**
     * Last date the template should generate transactions.
     * Null means it runs indefinitely until manually deactivated.
     */
    private LocalDate rtEndDate;

    // ── Getters & Setters ─────────────────────────────────────────────────────

    public Long getBudgetID()                { return budgetID; }
    public void setBudgetID(Long budgetID)   { this.budgetID = budgetID; }

    public Long getGoalID()              { return goalID; }
    public void setGoalID(Long goalID)   { this.goalID = goalID; }

    public String getRTransactionType()                          { return rTransactionType; }
    public void   setRTransactionType(String rTransactionType)   { this.rTransactionType = rTransactionType; }

    public String getRTransactionName()                          { return rTransactionName; }
    public void   setRTransactionName(String rTransactionName)   { this.rTransactionName = rTransactionName; }

    public String getRTransactionNote()                          { return rTransactionNote; }
    public void   setRTransactionNote(String rTransactionNote)   { this.rTransactionNote = rTransactionNote; }

    public BigDecimal getRTransactionAmount()                              { return rTransactionAmount; }
    public void       setRTransactionAmount(BigDecimal rTransactionAmount) { this.rTransactionAmount = rTransactionAmount; }

    public int  getRecurringDay()                  { return recurringDay; }
    public void setRecurringDay(int recurringDay)   { this.recurringDay = recurringDay; }

    public LocalDate getRtStartDate()                      { return rtStartDate; }
    public void      setRtStartDate(LocalDate rtStartDate) { this.rtStartDate = rtStartDate; }

    public LocalDate getRtEndDate()                    { return rtEndDate; }
    public void      setRtEndDate(LocalDate rtEndDate) { this.rtEndDate = rtEndDate; }
}