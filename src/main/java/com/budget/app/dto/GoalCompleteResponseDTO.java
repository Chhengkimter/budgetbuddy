package com.budget.app.dto;

import java.math.BigDecimal;

/**
 * Response wrapper for goal completion with transferred amount.
 */
public class GoalCompleteResponseDTO {

    private GoalResponseDTO goal;
    private BigDecimal transferredAmount;
    private String message;

    public GoalCompleteResponseDTO() {}

    public GoalCompleteResponseDTO(GoalResponseDTO goal, BigDecimal transferredAmount, String message) {
        this.goal = goal;
        this.transferredAmount = transferredAmount;
        this.message = message;
    }

    public GoalResponseDTO getGoal() {
        return goal;
    }

    public void setGoal(GoalResponseDTO goal) {
        this.goal = goal;
    }

    public BigDecimal getTransferredAmount() {
        return transferredAmount;
    }

    public void setTransferredAmount(BigDecimal transferredAmount) {
        this.transferredAmount = transferredAmount;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
