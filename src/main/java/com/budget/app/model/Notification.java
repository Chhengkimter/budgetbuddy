package com.budget.app.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Title is required")
    @Column(nullable = false, length = 150)
    private String title;

    @NotBlank(message = "Message is required")
    @Column(nullable = false, length = 500)
    private String message;

    @NotNull(message = "Type is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;

    @NotNull(message = "Read status is required")
    @Column(nullable = false)
    private Boolean isRead;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // A notification belongs to one user
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // ── Constructors ──────────────────────────────────
    public Notification() {
        this.isRead = false;
        this.type = NotificationType.GENERAL;
        this.createdAt = LocalDateTime.now();
    }

    public Notification(String title, String message, User user) {
        this();
        this.title = title;
        this.message = message;
        this.user = user;
    }

    public Notification(String title, String message, NotificationType type, User user) {
        this();
        this.title = title;
        this.message = message;
        this.type = type;
        this.user = user;
    }

    public Notification(String title, String message, NotificationType type, Boolean isRead, User user) {
        this.title = title;
        this.message = message;
        this.type = type;
        this.isRead = isRead;
        this.user = user;
        this.createdAt = LocalDateTime.now();
    }

    // Auto-set timestamp before saving
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.isRead == null) {
            this.isRead = false;
        }
        if (this.type == null) {
            this.type = NotificationType.GENERAL;
        }
    }

    // ── Getters & Setters ─────────────────────────────
    public Long getId()                                  { return id; }
    public void setId(Long id)                           { this.id = id; }

    public String getTitle()                             { return title; }
    public void setTitle(String title)                   { this.title = title; }

    public String getMessage()                           { return message; }
    public void setMessage(String message)               { this.message = message; }

    public NotificationType getType()                    { return type; }
    public void setType(NotificationType type)           { this.type = type; }

    public Boolean getIsRead()                           { return isRead; }
    public void setIsRead(Boolean isRead)                { this.isRead = isRead; }

    public LocalDateTime getCreatedAt()                  { return createdAt; }
    public void setCreatedAt(LocalDateTime dt)           { this.createdAt = dt; }

    public User getUser()                                { return user; }
    public void setUser(User user)                       { this.user = user; }

    // ── Helper Methods ────────────────────────────────
    /**
     * Mark notification as read
     */
    public void markAsRead() {
        this.isRead = true;
    }

    /**
     * Mark notification as unread
     */
    public void markAsUnread() {
        this.isRead = false;
    }

    /**
     * Toggle read status
     */
    public void toggleReadStatus() {
        this.isRead = !this.isRead;
    }

    /**
     * Check if notification is unread
     * @return true if not read
     */
    public boolean isUnread() {
        return !this.isRead;
    }

    @Override
    public String toString() {
        return "Notification{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", type=" + type +
                ", isRead=" + isRead +
                ", createdAt=" + createdAt +
                '}';
    }
}
