package com.budget.app.dto;

import com.budget.app.model.Budget;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class BudgetDTO {

    private Long id;
    private String name;
    private String category;
    private String description;
    private BigDecimal totalAmount;
    private BigDecimal spentAmount;
    private BigDecimal remainingBalance;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean isActive;

    public BudgetDTO(Budget budget) {
        this.id = budget.getId();
        this.name = budget.getName();
        this.category = budget.getCategory();
        this.description = budget.getDescription();
        this.totalAmount = budget.getTotalAmount();
        this.spentAmount = budget.getSpentAmount();
        this.remainingBalance = budget.getRemainingBalance();
        this.createdAt = budget.getCreatedAt();
        this.updatedAt = budget.getUpdatedAt();
        this.isActive = budget.getIsActive();
    }

    public BudgetDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public BigDecimal getSpentAmount() { return spentAmount; }
    public void setSpentAmount(BigDecimal spentAmount) { this.spentAmount = spentAmount; }

    public BigDecimal getRemainingBalance() { return remainingBalance; }
    public void setRemainingBalance(BigDecimal remainingBalance) { this.remainingBalance = remainingBalance; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
}
