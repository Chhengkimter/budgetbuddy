package com.budget.app.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "budgets")
public class Budget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Budget name is required")
    @Column(nullable = false)
    private String name;

    @NotBlank(message = "Category is required")
    @Column(nullable = false)
    private String category;

    @Column
    private String description;

    @Positive(message = "Amount must be positive")
    @Column(name = "total_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "spent_amount", precision = 12, scale = 2)
    private BigDecimal spentAmount = BigDecimal.ZERO;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "budget", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Transaction> transactions;

    public Budget() {}

    public Budget(String name, String category, BigDecimal totalAmount, User user) {
        this.name        = name;
        this.category    = category;
        this.totalAmount = totalAmount;
        this.user        = user;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt   = LocalDateTime.now();
        this.updatedAt   = LocalDateTime.now();
        this.isActive    = true;
        this.spentAmount = BigDecimal.ZERO;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    @Transient
    public BigDecimal getRemainingBalance() {
        if (totalAmount == null) return BigDecimal.ZERO;
        return totalAmount.subtract(spentAmount != null ? spentAmount : BigDecimal.ZERO);
    }

    public Long getId()                             { return id; }
    public void setId(Long id)                      { this.id = id; }

    public String getName()                         { return name; }
    public void setName(String name)                { this.name = name; }

    public String getCategory()                     { return category; }
    public void setCategory(String category)        { this.category = category; }

    public String getDescription()                  { return description; }
    public void setDescription(String description)  { this.description = description; }

    public BigDecimal getTotalAmount()              { return totalAmount; }
    public void setTotalAmount(BigDecimal amount)   { this.totalAmount = amount; }

    public BigDecimal getSpentAmount()              { return spentAmount; }
    public void setSpentAmount(BigDecimal spent)    { this.spentAmount = spent; }

    public LocalDateTime getCreatedAt()             { return createdAt; }
    public void setCreatedAt(LocalDateTime dt)      { this.createdAt = dt; }

    public LocalDateTime getUpdatedAt()             { return updatedAt; }
    public void setUpdatedAt(LocalDateTime dt)      { this.updatedAt = dt; }

    public Boolean getIsActive()                    { return isActive; }
    public void setIsActive(Boolean isActive)       { this.isActive = isActive; }

    public User getUser()                           { return user; }
    public void setUser(User user)                  { this.user = user; }

    public List<Transaction> getTransactions()      { return transactions; }
    public void setTransactions(List<Transaction> t){ this.transactions = t; }

    @Override
    public String toString() {
        return "Budget{id=" + id + ", name='" + name + "', category='" + category + "', total=" + totalAmount + "}";
    }
}
