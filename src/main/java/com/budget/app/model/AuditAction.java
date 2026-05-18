package com.budget.app.model;

public enum AuditAction {
    CREATE("Record Created"),
    UPDATE("Record Updated"),
    DELETE("Record Deleted");

    private final String displayName;

    AuditAction(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
