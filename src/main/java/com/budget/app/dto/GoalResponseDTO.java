package com.budget.app.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Represents a single goal card on the goals page.
 * `currentSaved` is calculated from transactions (not stored on the entity by default).
 */
public class GoalResponseDTO {

    private Long       goalID;
    private Long       userID;
    private String     goalName;
    private BigDecimal goalAmount;      // total target
    private BigDecimal currentSaved;    // computed: SUM of saving transactions
    private BigDecimal monthlyTarget;
    private Boolean    autoMonthly;
    private Boolean    isCompleted;
    private LocalDate  goalCreatedDate;
    private LocalDate  goalTargetDate;
    private LocalDate  goalFinishedDate;
    private double     progressPercent; // 0-100, computed as double
    private BigDecimal remainingAmount;

    // ── Getters & Setters ─────────────────────────────────────────────────────

    public Long getGoalID()                             { return goalID; }
    public void setGoalID(Long goalID)                  { this.goalID = goalID; }

    public Long getUserID()                             { return userID; }
    public void setUserID(Long userID)                  { this.userID = userID; }

    public String getGoalName()                         { return goalName; }
    public void   setGoalName(String goalName)          { this.goalName = goalName; }

    public BigDecimal getGoalAmount()                   { return goalAmount; }
    public void       setGoalAmount(BigDecimal a)       { this.goalAmount = a; }

    public BigDecimal getCurrentSaved()                 { return currentSaved; }
    public void       setCurrentSaved(BigDecimal a)     { this.currentSaved = a; }

    public BigDecimal getMonthlyTarget()                { return monthlyTarget; }
    public void       setMonthlyTarget(BigDecimal m)    { this.monthlyTarget = m; }

    public Boolean getAutoMonthly()                      { return autoMonthly; }
    public void    setAutoMonthly(Boolean b)             { this.autoMonthly = b; }

    public Boolean getIsCompleted()                      { return isCompleted; }
    public void    setIsCompleted(Boolean b)             { this.isCompleted = b; }

    public LocalDate getGoalCreatedDate()               { return goalCreatedDate; }
    public void      setGoalCreatedDate(LocalDate d)    { this.goalCreatedDate = d; }

    public LocalDate getGoalTargetDate()                { return goalTargetDate; }
    public void      setGoalTargetDate(LocalDate d)     { this.goalTargetDate = d; }

    public LocalDate getGoalFinishedDate()              { return goalFinishedDate; }
    public void      setGoalFinishedDate(LocalDate d)   { this.goalFinishedDate = d; }

    public double getProgressPercent()                  { return progressPercent; }
    public void   setProgressPercent(double p)          { this.progressPercent = p; }

    public BigDecimal getRemainingAmount()              { return remainingAmount; }
    public void       setRemainingAmount(BigDecimal r)  { this.remainingAmount = r; }
}