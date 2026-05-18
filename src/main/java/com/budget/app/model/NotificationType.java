package com.budget.app.model;

public enum NotificationType {
    BUDGET_ALERT("Budget Alert"),
    GOAL_REACHED("Goal Reached"),
    GOAL_REMINDER("Goal Reminder"),
    BILL_DUE("Bill Due"),
    BILL_OVERDUE("Bill Overdue"),
    GENERAL("General Notification");

    private final String displayName;

    NotificationType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
