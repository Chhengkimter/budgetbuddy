package com.budget.app.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Payload accepted by POST /api/goals and PUT /api/goals/{id}.
 */
public class GoalRequestDTO {

    private String    goalName;
    private BigDecimal goalAmount;   // target amount
    private LocalDate goalTargetDate;
    private BigDecimal monthlyTarget;
    private Boolean autoMonthly = false;

    // ── Getters & Setters ─────────────────────────────────────────────────────

    public String getGoalName()                       { return goalName; }
    public void   setGoalName(String goalName)        { this.goalName = goalName; }

    public BigDecimal getGoalAmount()                 { return goalAmount; }
    public void       setGoalAmount(BigDecimal a)     { this.goalAmount = a; }

    public LocalDate getGoalTargetDate()              { return goalTargetDate; }
    public void      setGoalTargetDate(LocalDate d)   { this.goalTargetDate = d; }

    public BigDecimal getMonthlyTarget()              { return monthlyTarget; }
    public void       setMonthlyTarget(BigDecimal m)  { this.monthlyTarget = m; }

    public Boolean    getAutoMonthly()                { return autoMonthly; }
    public void       setAutoMonthly(Boolean b)       { this.autoMonthly = b; }
}