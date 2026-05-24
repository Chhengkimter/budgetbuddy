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
@Table(name = "savings_goals")
public class SavingsGoal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "User ID is required")
    @Column(nullable = false)
    private Long userId;

    @NotBlank(message = "Savings goal name is required")
    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 255)
    private String description;

    @NotNull(message = "Target amount is required")
    @DecimalMin(value = "0.01", message = "Target amount must be greater than 0")
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal targetAmount;

    @NotNull(message = "Current amount is required")
    @DecimalMin(value = "0.0", message = "Current amount cannot be negative")
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal currentAmount;

    @Column(columnDefinition = "DATE")
    private LocalDate targetDate;

    @NotNull(message = "Status is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SavingsGoalStatus status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // ── Constructors ──────────────────────────────────
    public SavingsGoal() {
        this.currentAmount = BigDecimal.ZERO;
        this.status = SavingsGoalStatus.IN_PROGRESS;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public SavingsGoal(Long userId, String name, BigDecimal targetAmount) {
        this();
        this.userId = userId;
        this.name = name;
        this.targetAmount = targetAmount;
    }

    public SavingsGoal(Long userId, String name, String description, 
                       BigDecimal targetAmount, LocalDate targetDate) {
        this();
        this.userId = userId;
        this.name = name;
        this.description = description;
        this.targetAmount = targetAmount;
        this.targetDate = targetDate;
    }

    public SavingsGoal(Long userId, String name, String description, 
                       BigDecimal targetAmount, BigDecimal currentAmount, 
                       LocalDate targetDate, SavingsGoalStatus status) {
        this.userId = userId;
        this.name = name;
        this.description = description;
        this.targetAmount = targetAmount;
        this.currentAmount = currentAmount;
        this.targetDate = targetDate;
        this.status = status;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // ── Getters & Setters ─────────────────────────────
    public Long getId()                                          { return id; }
    public void setId(Long id)                                   { this.id = id; }

    public Long getUserId()                                      { return userId; }
    public void setUserId(Long userId)                           { this.userId = userId; }

    public String getName()                                      { return name; }
    public void setName(String name)                             { this.name = name; }

    public String getDescription()                               { return description; }
    public void setDescription(String description)               { this.description = description; }

    public BigDecimal getTargetAmount()                          { return targetAmount; }
    public void setTargetAmount(BigDecimal targetAmount)         { this.targetAmount = targetAmount; }

    public BigDecimal getCurrentAmount()                         { return currentAmount; }
    public void setCurrentAmount(BigDecimal currentAmount)       { this.currentAmount = currentAmount; }

    public LocalDate getTargetDate()                             { return targetDate; }
    public void setTargetDate(LocalDate targetDate)              { this.targetDate = targetDate; }

    public SavingsGoalStatus getStatus()                         { return status; }
    public void setStatus(SavingsGoalStatus status)              { this.status = status; }

    public LocalDateTime getCreatedAt()                          { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt)            { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt()                          { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt)            { this.updatedAt = updatedAt; }

    // ── Helper Methods ────────────────────────────────
    /**
     * Calculate the progress percentage
     * @return progress as a percentage (0-100)
     */
    public BigDecimal getProgressPercentage() {
        if (targetAmount.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return currentAmount
            .multiply(new BigDecimal("100"))
            .divide(targetAmount, 2, java.math.RoundingMode.HALF_UP);
    }

    /**
     * Get the remaining amount to reach the target
     * @return remaining amount
     */
    public BigDecimal getRemainingAmount() {
        return targetAmount.subtract(currentAmount);
    }

    /**
     * Check if the goal is completed
     * @return true if current amount >= target amount
     */
    public boolean isCompleted() {
        return currentAmount.compareTo(targetAmount) >= 0 
            || status == SavingsGoalStatus.COMPLETED;
    }

    /**
     * Add amount to current savings
     * @param amount amount to add
     */
    public void addSavings(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) > 0) {
            this.currentAmount = this.currentAmount.add(amount);
            this.updatedAt = LocalDateTime.now();

            // Auto-complete if target reached
            if (isCompleted() && status != SavingsGoalStatus.COMPLETED) {
                this.status = SavingsGoalStatus.COMPLETED;
            }
        }
    }

    @Override
    public String toString() {
        return "SavingsGoal{" +
                "id=" + id +
                ", userId=" + userId +
                ", name='" + name + '\'' +
                ", targetAmount=" + targetAmount +
                ", currentAmount=" + currentAmount +
                ", status=" + status +
                ", targetDate=" + targetDate +
                ", createdAt=" + createdAt +
                '}';
    }
}
