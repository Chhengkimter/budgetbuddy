package com.budget.app.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "goal_contributions")
public class GoalContribution {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(length = 255)
    private String notes;

    @Column(name = "contributed_at")
    private LocalDateTime contributedAt;

    // A contribution belongs to one savings goal
    @ManyToOne
    @JoinColumn(name = "goal_id", nullable = false)
    private SavingsGoal goal;

    // ── Constructors ──────────────────────────────────
    public GoalContribution() {}

    public GoalContribution(BigDecimal amount, SavingsGoal goal) {
        this.amount = amount;
        this.goal = goal;
        this.contributedAt = LocalDateTime.now();
    }

    public GoalContribution(BigDecimal amount, String notes, SavingsGoal goal) {
        this.amount = amount;
        this.notes = notes;
        this.goal = goal;
        this.contributedAt = LocalDateTime.now();
    }

    // Auto-set timestamp before saving
    @PrePersist
    protected void onCreate() {
        this.contributedAt = LocalDateTime.now();
    }

    // ── Getters & Setters ─────────────────────────────
    public Long getId()                                  { return id; }
    public void setId(Long id)                           { this.id = id; }

    public BigDecimal getAmount()                        { return amount; }
    public void setAmount(BigDecimal amount)             { this.amount = amount; }

    public String getNotes()                             { return notes; }
    public void setNotes(String notes)                   { this.notes = notes; }

    public LocalDateTime getContributedAt()              { return contributedAt; }
    public void setContributedAt(LocalDateTime dt)       { this.contributedAt = dt; }

    public SavingsGoal getGoal()                         { return goal; }
    public void setGoal(SavingsGoal goal)                { this.goal = goal; }

    @Override
    public String toString() {
        return "GoalContribution{" +
                "id=" + id +
                ", amount=" + amount +
                ", goalId=" + (goal != null ? goal.getId() : null) +
                ", contributedAt=" + contributedAt +
                '}';
    }
}
