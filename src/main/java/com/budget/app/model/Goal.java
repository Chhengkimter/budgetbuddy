package com.budget.app.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "Goal")
public class Goal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "GoalID")
    private Long goalID;

    @Column(name = "UserID", nullable = false)
    private Long userID;

    @Column(name = "GoalName", nullable = false)
    private String goalName;

    @Column(name = "GoalAmount", nullable = false, precision = 15, scale = 2)
    private BigDecimal goalAmount;

    @Column(name = "GoalCreatedDate")
    private LocalDate goalCreatedDate;

    @Column(name = "GoalTargetDate")
    private LocalDate goalTargetDate;

    @Column(name = "GoalFinishedDate")
    private LocalDate goalFinishedDate;

    @Column(name = "CurrentSaved", nullable = false, precision = 15, scale = 2)
    private BigDecimal currentSaved = BigDecimal.ZERO;

    @Column(name = "MonthlyTarget")
    private BigDecimal monthlyTarget;

    @Column(name = "IsCompleted", nullable = false)
    private Boolean isCompleted = false;

    @Column(name = "AutoMonthly", nullable = false)
    private Boolean autoMonthly = false;
    public Goal() {}

    public Goal(Long userID, String goalName, BigDecimal goalAmount, LocalDate goalTargetDate) {
        this.userID          = userID;
        this.goalName        = goalName;
        this.goalAmount      = goalAmount;
        this.goalTargetDate  = goalTargetDate;
        this.goalCreatedDate = LocalDate.now();
    }

    public Long      getGoalID()                        { return goalID; }
    public void      setGoalID(Long goalID)             { this.goalID = goalID; }

    public Long      getUserID()                        { return userID; }
    public void      setUserID(Long userID)             { this.userID = userID; }

    public String    getGoalName()                      { return goalName; }
    public void      setGoalName(String goalName)       { this.goalName = goalName; }

    public BigDecimal getGoalAmount()                   { return goalAmount; }
    public void       setGoalAmount(BigDecimal a)       { this.goalAmount = a; }

    public LocalDate getGoalCreatedDate()               { return goalCreatedDate; }
    public void      setGoalCreatedDate(LocalDate d)    { this.goalCreatedDate = d; }

    public LocalDate getGoalTargetDate()                { return goalTargetDate; }
    public void      setGoalTargetDate(LocalDate d)     { this.goalTargetDate = d; }

    public LocalDate getGoalFinishedDate()              { return goalFinishedDate; }
    public void      setGoalFinishedDate(LocalDate d)   { this.goalFinishedDate = d; }

    public BigDecimal getCurrentSaved()                 { return currentSaved; }
    public void       setCurrentSaved(BigDecimal v)     { this.currentSaved = v; }

    public BigDecimal getMonthlyTarget()                { return monthlyTarget; }
    public void       setMonthlyTarget(BigDecimal v)    { this.monthlyTarget = v; }

    public Boolean    getIsCompleted()                  { return isCompleted; }
    public void       setIsCompleted(Boolean b)         { this.isCompleted = b; }

    public Boolean    getAutoMonthly()                  { return autoMonthly; }
    public void       setAutoMonthly(Boolean b)         { this.autoMonthly = b; }
}