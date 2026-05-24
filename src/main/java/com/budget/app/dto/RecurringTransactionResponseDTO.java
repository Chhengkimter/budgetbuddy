package com.budget.app.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Outbound payload for all /api/recurring endpoints.
 *
 * Includes all DB columns from the RecurringTransaction table plus
 * resolved display names (budgetName, goalName) for the frontend.
 *
 * rtIsActive = false means the template has been soft-deactivated via
 * PATCH /api/recurring/{id}/deactivate — its previously generated
 * Transaction rows are preserved.
 */
public class RecurringTransactionResponseDTO {

    private Long recurringID;
    private Long userID;

    private Long   budgetID;
    private String budgetName;   // resolved display name

    private Long   goalID;
    private String goalName;     // resolved display name

    /** INCOME | EXPENSE | SAVING */
    private String     rTransactionType;
    private String     rTransactionName;
    private String     rTransactionNote;
    private BigDecimal rTransactionAmount;

    /** Day of month (1–28) the scheduler fires. */
    private int recurringDay;

    private LocalDate rtStartDate;
    private LocalDate rtEndDate;           // null = runs indefinitely
    private boolean   rtIsActive;
    private LocalDate rtLastGeneratedDate; // null until first generation

    // ── Getters & Setters ─────────────────────────────────────────────────────

    public Long getRecurringID()                     { return recurringID; }
    public void setRecurringID(Long recurringID)     { this.recurringID = recurringID; }

    public Long getUserID()              { return userID; }
    public void setUserID(Long userID)   { this.userID = userID; }

    public Long getBudgetID()                { return budgetID; }
    public void setBudgetID(Long budgetID)   { this.budgetID = budgetID; }

    public String getBudgetName()                  { return budgetName; }
    public void   setBudgetName(String budgetName) { this.budgetName = budgetName; }

    public Long getGoalID()              { return goalID; }
    public void setGoalID(Long goalID)   { this.goalID = goalID; }

    public String getGoalName()                { return goalName; }
    public void   setGoalName(String goalName) { this.goalName = goalName; }

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

    public boolean isRtIsActive()                  { return rtIsActive; }
    public void    setRtIsActive(boolean rtIsActive) { this.rtIsActive = rtIsActive; }

    public LocalDate getRtLastGeneratedDate()                              { return rtLastGeneratedDate; }
    public void      setRtLastGeneratedDate(LocalDate rtLastGeneratedDate) { this.rtLastGeneratedDate = rtLastGeneratedDate; }
}