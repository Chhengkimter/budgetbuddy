package com.budget.app.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_settings")
public class UserSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "User ID is required")
    @Column(nullable = false, unique = true)
    private Long userId;

    @NotBlank(message = "Currency is required")
    @Column(nullable = false, length = 10)
    private String currency;

    @NotBlank(message = "Language is required")
    @Column(nullable = false, length = 10)
    private String language;

    @NotNull(message = "Email alerts setting is required")
    @Column(nullable = false)
    private Boolean emailAlerts;

    @NotNull(message = "Budget alerts setting is required")
    @Column(nullable = false)
    private Boolean budgetAlerts;

    @NotNull(message = "Budget alert threshold is required")
    @DecimalMin(value = "0.0", message = "Budget alert threshold must be at least 0")
    @DecimalMax(value = "100.0", message = "Budget alert threshold cannot exceed 100")
    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal budgetAlertThreshold;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // ── Constructors ──────────────────────────────────
    public UserSettings() {
        this.currency = "USD";
        this.language = "en";
        this.emailAlerts = true;
        this.budgetAlerts = true;
        this.budgetAlertThreshold = new BigDecimal("80.00");
        this.updatedAt = LocalDateTime.now();
    }

    public UserSettings(Long userId) {
        this();
        this.userId = userId;
    }

    public UserSettings(Long userId, String currency, String language, 
                        Boolean emailAlerts, Boolean budgetAlerts, 
                        BigDecimal budgetAlertThreshold) {
        this.userId = userId;
        this.currency = currency;
        this.language = language;
        this.emailAlerts = emailAlerts;
        this.budgetAlerts = budgetAlerts;
        this.budgetAlertThreshold = budgetAlertThreshold;
        this.updatedAt = LocalDateTime.now();
    }

    // ── Getters & Setters ─────────────────────────────
    public Long getId()                                          { return id; }
    public void setId(Long id)                                   { this.id = id; }

    public Long getUserId()                                      { return userId; }
    public void setUserId(Long userId)                           { this.userId = userId; }

    public String getCurrency()                                  { return currency; }
    public void setCurrency(String currency)                     { this.currency = currency; }

    public String getLanguage()                                  { return language; }
    public void setLanguage(String language)                     { this.language = language; }

    public Boolean getEmailAlerts()                              { return emailAlerts; }
    public void setEmailAlerts(Boolean emailAlerts)              { this.emailAlerts = emailAlerts; }

    public Boolean getBudgetAlerts()                             { return budgetAlerts; }
    public void setBudgetAlerts(Boolean budgetAlerts)            { this.budgetAlerts = budgetAlerts; }

    public BigDecimal getBudgetAlertThreshold()                  { return budgetAlertThreshold; }
    public void setBudgetAlertThreshold(BigDecimal threshold)    { this.budgetAlertThreshold = threshold; }

    public LocalDateTime getUpdatedAt()                          { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt)            { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        return "UserSettings{" +
                "id=" + id +
                ", userId=" + userId +
                ", currency='" + currency + '\'' +
                ", language='" + language + '\'' +
                ", emailAlerts=" + emailAlerts +
                ", budgetAlerts=" + budgetAlerts +
                ", budgetAlertThreshold=" + budgetAlertThreshold +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
