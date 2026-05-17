package com.budget.app.model;

public enum RecurringTransactionFrequency {
    DAILY("Every Day", 1),
    WEEKLY("Every Week", 7),
    MONTHLY("Every Month", 30),
    YEARLY("Every Year", 365);

    private final String displayName;
    private final int days;

    RecurringTransactionFrequency(String displayName, int days) {
        this.displayName = displayName;
        this.days = days;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getDays() {
        return days;
    }
}
