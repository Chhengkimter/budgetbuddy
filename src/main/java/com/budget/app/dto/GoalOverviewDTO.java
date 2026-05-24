package com.budget.app.dto;

import java.math.BigDecimal;
import java.util.List;

/**
 * Full payload returned by GET /api/goals/overview.
 * Powers both stat cards and the goal grid on goals.html.
 */
public class GoalOverviewDTO {

    /** Sum of all Saving transactions for this user, all-time. */
    private BigDecimal totalSaved;

    /** BudgetLimit of the "Saving" budget for the requested month/year. */
    private BigDecimal monthlyTarget;

    /** Sum of Saving transactions for the requested month/year. */
    private BigDecimal monthlyDeposited;

    /** 0-100 computed progress for the monthly bar. */
    private int monthlyProgressPercent;

    /** All active (unfinished) goals with their per-goal progress. */
    private List<GoalResponseDTO> goalsList;

    // ── Getters & Setters ─────────────────────────────────────────────────────

    public BigDecimal getTotalSaved()                         { return totalSaved; }
    public void       setTotalSaved(BigDecimal v)             { this.totalSaved = v; }

    public BigDecimal getMonthlyTarget()                      { return monthlyTarget; }
    public void       setMonthlyTarget(BigDecimal v)          { this.monthlyTarget = v; }

    public BigDecimal getMonthlyDeposited()                   { return monthlyDeposited; }
    public void       setMonthlyDeposited(BigDecimal v)       { this.monthlyDeposited = v; }

    public int  getMonthlyProgressPercent()                   { return monthlyProgressPercent; }
    public void setMonthlyProgressPercent(int v)              { this.monthlyProgressPercent = v; }

    public List<GoalResponseDTO> getGoalsList()               { return goalsList; }
    public void setGoalsList(List<GoalResponseDTO> goalsList) { this.goalsList = goalsList; }
}