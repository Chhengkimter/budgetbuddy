package com.budget.app.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Represents a single goal card on the goals page.
 * savedAmount is calculated from transactions (not stored on the entity).
 */
public class GoalResponseDTO {

    private Long       goalID;
    private String     goalName;
    private BigDecimal goalAmount;      // total target
    private BigDecimal savedAmount;     // computed: SUM of saving transactions
    private LocalDate  goalCreatedDate;
    private LocalDate  goalTargetDate;
    private LocalDate  goalFinishedDate;
    private int        progressPercent; // 0-100, computed

    // ── Getters & Setters ─────────────────────────────────────────────────────

    public Long getGoalID()                             { return goalID; }
    public void setGoalID(Long goalID)                  { this.goalID = goalID; }

    public String getGoalName()                         { return goalName; }
    public void   setGoalName(String goalName)          { this.goalName = goalName; }

    public BigDecimal getGoalAmount()                   { return goalAmount; }
    public void       setGoalAmount(BigDecimal a)       { this.goalAmount = a; }

    public BigDecimal getSavedAmount()                  { return savedAmount; }
    public void       setSavedAmount(BigDecimal a)      { this.savedAmount = a; }

    public LocalDate getGoalCreatedDate()               { return goalCreatedDate; }
    public void      setGoalCreatedDate(LocalDate d)    { this.goalCreatedDate = d; }

    public LocalDate getGoalTargetDate()                { return goalTargetDate; }
    public void      setGoalTargetDate(LocalDate d)     { this.goalTargetDate = d; }

    public LocalDate getGoalFinishedDate()              { return goalFinishedDate; }
    public void      setGoalFinishedDate(LocalDate d)   { this.goalFinishedDate = d; }

    public int  getProgressPercent()                    { return progressPercent; }
    public void setProgressPercent(int p)               { this.progressPercent = p; }
}