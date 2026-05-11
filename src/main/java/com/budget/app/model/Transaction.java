package com.budget.app.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
public class Transaction {

    public enum Type {
        INCOME, EXPENSE
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Description is required")
    @Column(nullable = false)
    private String description;

    @Positive(message = "Amount must be positive")
    @Column(nullable = false)
    private Double amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Type type;  

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "budget_id", nullable = false)
    private Budget budget;

    public Transaction() {}

    public Transaction(String description, Double amount, Type type, Budget budget) {
        this.description = description;
        this.amount      = amount;
        this.type        = type;
        this.budget      = budget;
        this.createdAt   = LocalDateTime.now();
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public Long getId()                         { return id; }
    public void setId(Long id)                  { this.id = id; }

    public String getDescription()              { return description; }
    public void setDescription(String desc)     { this.description = desc; }

    public Double getAmount()                   { return amount; }
    public void setAmount(Double amount)        { this.amount = amount; }

    public Type getType()                       { return type; }
    public void setType(Type type)              { this.type = type; }

    public LocalDateTime getCreatedAt()         { return createdAt; }
    public void setCreatedAt(LocalDateTime dt)  { this.createdAt = dt; }

    public Budget getBudget()                   { return budget; }
    public void setBudget(Budget budget)        { this.budget = budget; }

    @Override
    public String toString() {
        return "Transaction{id=" + id + ", desc='" + description + "', amount=" + amount + ", type=" + type + "}";
    }
}
