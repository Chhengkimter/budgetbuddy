package com.budget.app.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Action is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuditAction action;

    @NotBlank(message = "Table name is required")
    @Column(nullable = false, length = 100)
    private String tableName;

    @NotNull(message = "Record ID is required")
    @Column(nullable = false)
    private Long recordId;

    @Column(columnDefinition = "JSON")
    private String oldValue;

    @Column(columnDefinition = "JSON")
    private String newValue;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // A audit log may belong to a user (nullable - ON DELETE SET NULL)
    @ManyToOne(optional = true)
    @JoinColumn(name = "user_id", nullable = true)
    private User user;

    // ── Constructors ──────────────────────────────────
    public AuditLog() {
        this.createdAt = LocalDateTime.now();
    }

    public AuditLog(AuditAction action, String tableName, Long recordId) {
        this();
        this.action = action;
        this.tableName = tableName;
        this.recordId = recordId;
    }

    public AuditLog(AuditAction action, String tableName, Long recordId, String oldValue, String newValue) {
        this();
        this.action = action;
        this.tableName = tableName;
        this.recordId = recordId;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    public AuditLog(AuditAction action, String tableName, Long recordId, String oldValue, String newValue, User user) {
        this();
        this.action = action;
        this.tableName = tableName;
        this.recordId = recordId;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.user = user;
    }

    // Auto-set timestamp before saving
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    // ── Getters & Setters ─────────────────────────────
    public Long getId()                                  { return id; }
    public void setId(Long id)                           { this.id = id; }

    public AuditAction getAction()                       { return action; }
    public void setAction(AuditAction action)            { this.action = action; }

    public String getTableName()                         { return tableName; }
    public void setTableName(String tableName)           { this.tableName = tableName; }

    public Long getRecordId()                            { return recordId; }
    public void setRecordId(Long recordId)               { this.recordId = recordId; }

    public String getOldValue()                          { return oldValue; }
    public void setOldValue(String oldValue)             { this.oldValue = oldValue; }

    public String getNewValue()                          { return newValue; }
    public void setNewValue(String newValue)             { this.newValue = newValue; }

    public LocalDateTime getCreatedAt()                  { return createdAt; }
    public void setCreatedAt(LocalDateTime dt)           { this.createdAt = dt; }

    public User getUser()                                { return user; }
    public void setUser(User user)                       { this.user = user; }

    // ── Helper Methods ────────────────────────────────
    /**
     * Check if this is a CREATE action
     * @return true if action is CREATE
     */
    public boolean isCreate() {
        return action == AuditAction.CREATE;
    }

    /**
     * Check if this is an UPDATE action
     * @return true if action is UPDATE
     */
    public boolean isUpdate() {
        return action == AuditAction.UPDATE;
    }

    /**
     * Check if this is a DELETE action
     * @return true if action is DELETE
     */
    public boolean isDelete() {
        return action == AuditAction.DELETE;
    }

    /**
     * Get user ID safely (handles null user)
     * @return user ID or null
     */
    public Long getUserId() {
        return user != null ? user.getId() : null;
    }

    /**
     * Get user email safely (handles null user)
     * @return user email or "SYSTEM"
     */
    public String getUserEmail() {
        return user != null ? user.getEmail() : "SYSTEM";
    }

    /**
     * Create full audit log description
     * @return formatted audit log description
     */
    public String getDescription() {
        return String.format("%s - Table: %s, Record: %d, User: %s",
                action.getDisplayName(),
                tableName,
                recordId,
                getUserEmail());
    }

    @Override
    public String toString() {
        return "AuditLog{" +
                "id=" + id +
                ", action=" + action +
                ", tableName='" + tableName + '\'' +
                ", recordId=" + recordId +
                ", userId=" + getUserId() +
                ", createdAt=" + createdAt +
                '}';
    }
}
