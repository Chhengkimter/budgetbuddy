package com.budget.app.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "Transaction")
public class Transaction {

    public Transaction(Long userID, Long budgetID, Long goalID,
                   String transactionType, LocalDate transactionDate,
                   String transactionName, String transactionNote,
                   BigDecimal transactionAmount) {
    this.userID            = userID;
    this.budgetID          = budgetID;
    this.goalID            = goalID;
    this.transactionType   = transactionType;
    this.transactionDate   = transactionDate;
    this.transactionName   = transactionName;
    this.transactionNote   = transactionNote;
    this.transactionAmount = transactionAmount;
}
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TransactionID")
    private Long transactionID;

    @Column(name = "UserID", nullable = false)
    private Long userID;

    /** Nullable — a transaction may not be linked to any budget. */
    @Column(name = "BudgetID")
    private Long budgetID;

    /** Nullable — only populated for Saving-type transactions linked to a goal. */
    @Column(name = "GoalID")
    private Long goalID;

    @Column(name = "RecurringID")
    private Long recurringID;

    /** "Income" | "Spending" | "Saving" */
    @Column(name = "TransactionType", nullable = false)
    private String transactionType;

    @Column(name = "TransactionDate", nullable = false)
    private LocalDate transactionDate;

    @Column(name = "TransactionName")
    private String transactionName;

    @Column(name = "TransactionNote")
    private String transactionNote;

    @Column(name = "TransactionAmount", nullable = false, precision = 15, scale = 2)
    private BigDecimal transactionAmount;

    public Transaction() {}

    public Long        getTransactionID()                            { return transactionID; }
    public void        setTransactionID(Long transactionID)          { this.transactionID = transactionID; }

    public Long        getUserID()                                   { return userID; }
    public void        setUserID(Long userID)                        { this.userID = userID; }

    public Long        getBudgetID()                                 { return budgetID; }
    public void        setBudgetID(Long budgetID)                    { this.budgetID = budgetID; }

    public Long        getGoalID()                                   { return goalID; }
    public void        setGoalID(Long goalID)                        { this.goalID = goalID; }

    public String      getTransactionType()                          { return transactionType; }
    public void        setTransactionType(String transactionType)    { this.transactionType = transactionType; }

    public LocalDate   getTransactionDate()                          { return transactionDate; }
    public void        setTransactionDate(LocalDate transactionDate) { this.transactionDate = transactionDate; }

    public String      getTransactionName()                          { return transactionName; }
    public void        setTransactionName(String transactionName)    { this.transactionName = transactionName; }

    public String      getTransactionNote()                          { return transactionNote; }
    public void        setTransactionNote(String transactionNote)    { this.transactionNote = transactionNote; }

    public BigDecimal  getTransactionAmount()                        { return transactionAmount; }
    public void        setTransactionAmount(BigDecimal amount)       { this.transactionAmount = amount; }

    public Long        getRecurringID()                              { return recurringID; }
    public void        setRecurringID(Long recurringID)               { this.recurringID = recurringID; }
}