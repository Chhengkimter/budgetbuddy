package com.budget.app.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.DecimalMin;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "recurring_transactions")
public class RecurringTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Description is required")
    @Column(nullable = false)
    private String description;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @NotNull(message = "Type is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Transaction.Type type;  // INCOME or EXPENSE

    @Column(length = 50)
    private String categoryTag;

    @NotNull(message = "Frequency is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RecurringTransactionFrequency frequency;

    @NotNull(message = "Next due date is required")
    @Column(nullable = false, columnDefinition = "DATE")
    private LocalDate nextDueDate;

    @Column(columnDefinition = "DATE")
    private LocalDate endDate;

    @NotNull(message = "Active status is required")
    @Column(nullable = false)
    private Boolean isActive;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // A recurring transaction belongs to one budget
    @ManyToOne
    @JoinColumn(name = "budget_id", nullable = false)
    private Budget budget;

    // ── Constructors ──────────────────────────────────
    public RecurringTransaction() {
        this.isActive = true;
        this.frequency = RecurringTransactionFrequency.MONTHLY;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public RecurringTransaction(String description, BigDecimal amount, Transaction.Type type, 
                                RecurringTransactionFrequency frequency, LocalDate nextDueDate, 
                                Budget budget) {
        this();
        this.description = description;
        this.amount = amount;
        this.type = type;
        this.frequency = frequency;
        this.nextDueDate = nextDueDate;
        this.budget = budget;
    }

    public RecurringTransaction(String description, BigDecimal amount, Transaction.Type type, 
                                String categoryTag, RecurringTransactionFrequency frequency, 
                                LocalDate nextDueDate, LocalDate endDate, Budget budget) {
        this();
        this.description = description;
        this.amount = amount;
        this.type = type;
        this.categoryTag = categoryTag;
        this.frequency = frequency;
        this.nextDueDate = nextDueDate;
        this.endDate = endDate;
        this.budget = budget;
    }

    // Auto-set timestamp before saving
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // ── Getters & Setters ─────────────────────────────
    public Long getId()                                              { return id; }
    public void setId(Long id)                                       { this.id = id; }

    public String getDescription()                                   { return description; }
    public void setDescription(String description)                   { this.description = description; }

    public BigDecimal getAmount()                                    { return amount; }
    public void setAmount(BigDecimal amount)                         { this.amount = amount; }

    public Transaction.Type getType()                                { return type; }
    public void setType(Transaction.Type type)                       { this.type = type; }

    public String getCategoryTag()                                   { return categoryTag; }
    public void setCategoryTag(String categoryTag)                   { this.categoryTag = categoryTag; }

    public RecurringTransactionFrequency getFrequency()              { return frequency; }
    public void setFrequency(RecurringTransactionFrequency freq)     { this.frequency = freq; }

    public LocalDate getNextDueDate()                                { return nextDueDate; }
    public void setNextDueDate(LocalDate date)                       { this.nextDueDate = date; }

    public LocalDate getEndDate()                                    { return endDate; }
    public void setEndDate(LocalDate date)                           { this.endDate = date; }

    public Boolean getIsActive()                                     { return isActive; }
    public void setIsActive(Boolean isActive)                        { this.isActive = isActive; }

    public LocalDateTime getCreatedAt()                              { return createdAt; }
    public void setCreatedAt(LocalDateTime dt)                       { this.createdAt = dt; }

    public LocalDateTime getUpdatedAt()                              { return updatedAt; }
    public void setUpdatedAt(LocalDateTime dt)                       { this.updatedAt = dt; }

    public Budget getBudget()                                        { return budget; }
    public void setBudget(Budget budget)                             { this.budget = budget; }

    // ── Helper Methods ────────────────────────────────
    /**
     * Check if this recurring transaction is still active
     * @return true if active and not expired
     */
    public boolean isStillActive() {
        if (!isActive) {
            return false;
        }
        if (endDate != null && LocalDate.now().isAfter(endDate)) {
            return false;
        }
        return true;
    }

    /**
     * Check if the next due date has passed
     * @return true if next due date is today or in the past
     */
    public boolean isDue() {
        return nextDueDate.isBefore(LocalDate.now()) || nextDueDate.isEqual(LocalDate.now());
    }

    /**
     * Calculate the next occurrence date based on frequency
     * @return the next due date after current next_due_date
     */
    public LocalDate calculateNextOccurrence() {
        LocalDate current = nextDueDate;
        return switch (frequency) {
            case DAILY -> current.plusDays(1);
            case WEEKLY -> current.plusWeeks(1);
            case MONTHLY -> current.plusMonths(1);
            case YEARLY -> current.plusYears(1);
        };
    }

    /**
     * Advance to next occurrence (called after transaction is processed)
     */
    public void advanceToNextOccurrence() {
        this.nextDueDate = calculateNextOccurrence();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Check if this recurring transaction has expired
     * @return true if end_date has passed
     */
    public boolean hasExpired() {
        return endDate != null && LocalDate.now().isAfter(endDate);
    }

    @Override
    public String toString() {
        return "RecurringTransaction{" +
                "id=" + id +
                ", desc='" + description + '\'' +
                ", amount=" + amount +
                ", type=" + type +
                ", frequency=" + frequency +
                ", nextDueDate=" + nextDueDate +
                ", isActive=" + isActive +
                '}';
    }
}
