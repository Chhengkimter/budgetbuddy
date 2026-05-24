package com.budget.app.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "Budget")
public class Budget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "BudgetID")
    private Long budgetID;

    @Column(name = "UserID", nullable = false)
    private Long userID;

    @Column(name = "BudgetName", nullable = false)
    private String budgetName;

    @Column(name = "BudgetLimit", nullable = false, precision = 15, scale = 2)
    private BigDecimal budgetLimit;

    @Column(name = "BudgetUpdated")
    private LocalDateTime budgetUpdated;

    @Column(name = "BudgetMonth", nullable = false)
    private Integer budgetMonth;

    @Column(name = "BudgetYear", nullable = false)
    private Integer budgetYear;

    @Column(name = "BudgetIsRecurring")
    private Boolean budgetIsRecurring = false;

    @Column(name = "BudgetLastGeneratedDate")
    private LocalDate budgetLastGeneratedDate;

    public Budget() {}

    public Budget(Long userID, String budgetName, BigDecimal budgetLimit,
                  int budgetMonth, int budgetYear, boolean isRecurring) {
        this.userID            = userID;
        this.budgetName        = budgetName;
        this.budgetLimit       = budgetLimit;
        this.budgetMonth       = budgetMonth;
        this.budgetYear        = budgetYear;
        this.budgetIsRecurring = isRecurring;
        this.budgetUpdated     = LocalDateTime.now();
    }

    public Long          getBudgetID()                            { return budgetID; }
    public void          setBudgetID(Long budgetID)               { this.budgetID = budgetID; }

    public Long          getUserID()                              { return userID; }
    public void          setUserID(Long userID)                   { this.userID = userID; }

    public String        getBudgetName()                          { return budgetName; }
    public void          setBudgetName(String budgetName)         { this.budgetName = budgetName; }

    public BigDecimal    getBudgetLimit()                         { return budgetLimit; }
    public void          setBudgetLimit(BigDecimal budgetLimit)   { this.budgetLimit = budgetLimit; }

    public LocalDateTime getBudgetUpdated()                       { return budgetUpdated; }
    public void          setBudgetUpdated(LocalDateTime d)        { this.budgetUpdated = d; }

    public Integer       getBudgetMonth()                         { return budgetMonth; }
    public void          setBudgetMonth(Integer budgetMonth)      { this.budgetMonth = budgetMonth; }

    public Integer       getBudgetYear()                          { return budgetYear; }
    public void          setBudgetYear(Integer budgetYear)        { this.budgetYear = budgetYear; }

    public Boolean       getBudgetIsRecurring()                   { return budgetIsRecurring; }
    public void          setBudgetIsRecurring(Boolean recurring)  { this.budgetIsRecurring = recurring; }

    public LocalDate     getBudgetLastGeneratedDate()             { return budgetLastGeneratedDate; }
    public void          setBudgetLastGeneratedDate(LocalDate d)  { this.budgetLastGeneratedDate = d; }
}