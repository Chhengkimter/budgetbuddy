package com.budget.app.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Request body for goal deposit endpoint.
 */
public class GoalDepositRequestDTO {

    private BigDecimal amount;
    private LocalDate depositDate;

    public GoalDepositRequestDTO() {}

    public GoalDepositRequestDTO(BigDecimal amount, LocalDate depositDate) {
        this.amount = amount;
        this.depositDate = depositDate;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDate getDepositDate() {
        return depositDate;
    }

    public void setDepositDate(LocalDate depositDate) {
        this.depositDate = depositDate;
    }
}
