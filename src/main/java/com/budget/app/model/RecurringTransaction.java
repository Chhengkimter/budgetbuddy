package com.budget.app.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "RecurringTransaction")
public class RecurringTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "RecurringID")
    private Long recurringID;

    @Column(name = "UserID", nullable = false)
    private Long userID;

    /** Null for SAVING and INCOME templates; links EXPENSE to a budget category. */
    @Column(name = "BudgetID")
    private Long budgetID;

    /**
     * Null for INCOME and EXPENSE templates.
     * Links SAVING templates to a specific goal so auto-generated deposits
     * count toward that goal's progress.
     */
    @Column(name = "GoalID")
    private Long goalID;

    /** INCOME | EXPENSE | SAVING */
    @Column(name = "RTransactionType", nullable = false)
    private String rTransactionType;

    @Column(name = "RTransactionName", nullable = false)
    private String rTransactionName;

    @Column(name = "RTransactionNote")
    private String rTransactionNote;

    @Column(name = "RTransactionAmount", nullable = false, precision = 15, scale = 2)
    private BigDecimal rTransactionAmount;

    /**
     * Day of month (1–28) on which the scheduler generates a Transaction row.
     * Capped at 28 to avoid issues with February.
     */
    @Column(name = "RecurringDay", nullable = false)
    private int recurringDay;

    @Column(name = "RTStartDate", nullable = false)
    private LocalDate rtStartDate;

    /** Null means the template runs indefinitely until manually deactivated. */
    @Column(name = "RTEndDate")
    private LocalDate rtEndDate;

    @Column(name = "RTIsActive", nullable = false)
    private boolean rtIsActive = true;

    /** Updated by the scheduler each time it successfully generates a Transaction. */
    @Column(name = "RTLastGeneratedDate")
    private LocalDate rtLastGeneratedDate;

    // ── Constructors ──────────────────────────────────────────────────────────

    public RecurringTransaction() {}

    public RecurringTransaction(Long userID, Long budgetID, Long goalID,
                                String rTransactionType, String rTransactionName,
                                String rTransactionNote, BigDecimal rTransactionAmount,
                                int recurringDay, LocalDate rtStartDate, LocalDate rtEndDate) {
        this.userID             = userID;
        this.budgetID           = budgetID;
        this.goalID             = goalID;
        this.rTransactionType   = rTransactionType;
        this.rTransactionName   = rTransactionName;
        this.rTransactionNote   = rTransactionNote;
        this.rTransactionAmount = rTransactionAmount;
        this.recurringDay       = recurringDay;
        this.rtStartDate        = rtStartDate;
        this.rtEndDate          = rtEndDate;
        this.rtIsActive         = true;
    }

    // ── Getters & Setters ─────────────────────────────────────────────────────

    public Long getRecurringID()                     { return recurringID; }
    public void setRecurringID(Long recurringID)     { this.recurringID = recurringID; }

    public Long getUserID()              { return userID; }
    public void setUserID(Long userID)   { this.userID = userID; }

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

    public boolean isRtIsActive()                    { return rtIsActive; }
    public void    setRtIsActive(boolean rtIsActive) { this.rtIsActive = rtIsActive; }

    public LocalDate getRtLastGeneratedDate()                              { return rtLastGeneratedDate; }
    public void      setRtLastGeneratedDate(LocalDate rtLastGeneratedDate) { this.rtLastGeneratedDate = rtLastGeneratedDate; }
}