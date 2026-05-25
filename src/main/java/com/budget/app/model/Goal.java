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

    // Alias property name for repositories that expect `id`
    public Long getId() { return this.goalID; }
    public void setId(Long id) { this.goalID = id; }

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
}