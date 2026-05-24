package com.budget.app.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
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
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Type type;  // INCOME or EXPENSE

    @Column(name = "category_tag")
    private String categoryTag;

    @Column
    private String notes;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "is_deleted")
    private Boolean isDeleted = false;

    @ManyToOne
    @JoinColumn(name = "budget_id", nullable = false)
    private Budget budget;

    public Transaction() {}

    public Transaction(String description, BigDecimal amount, Type type, Budget budget) {
        this.description = description;
        this.amount      = amount;
        this.type        = type;
        this.budget      = budget;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.isDeleted = false;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId()                           { return id; }
    public void setId(Long id)                    { this.id = id; }

    public String getDescription()                { return description; }
    public void setDescription(String desc)       { this.description = desc; }

    public BigDecimal getAmount()                 { return amount; }
    public void setAmount(BigDecimal amount)      { this.amount = amount; }

    public Type getType()                         { return type; }
    public void setType(Type type)                { this.type = type; }

    public String getCategoryTag()                { return categoryTag; }
    public void setCategoryTag(String tag)        { this.categoryTag = tag; }

    public String getNotes()                      { return notes; }
    public void setNotes(String notes)            { this.notes = notes; }

    public LocalDateTime getCreatedAt()           { return createdAt; }
    public void setCreatedAt(LocalDateTime dt)    { this.createdAt = dt; }

    public LocalDateTime getUpdatedAt()           { return updatedAt; }
    public void setUpdatedAt(LocalDateTime dt)    { this.updatedAt = dt; }

    public Boolean getIsDeleted()                 { return isDeleted; }
    public void setIsDeleted(Boolean isDeleted)   { this.isDeleted = isDeleted; }

    public Budget getBudget()                     { return budget; }
    public void setBudget(Budget budget)          { this.budget = budget; }

    @Override
    public String toString() {
        return "Transaction{id=" + id + ", desc='" + description + "', amount=" + amount + ", type=" + type + "}";
    }
}
