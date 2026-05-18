package com.budget.app.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity                          // ← ADDED: tells Spring this is a DB table
@Table(name = "categories")      // ← ADDED: names the table
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Category name is required")
    @Column(nullable = false)
    private String name;

    // ← FIXED: proper relationship instead of raw Long userId
    // This links to the actual User object, not just their ID number
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // ← FIXED: removed defaultValue (doesn't exist in @Column)
    // The = false on the field itself is the correct way to set a default
    @Column(name = "is_default", nullable = false)
    private Boolean isDefault = false;

    // ── Constructors ──────────────────────────────────
    public Category() {}

    public Category(String name, User user) {
        this.name   = name;
        this.user   = user;
    }

    // Constructor for default/seed categories
    public Category(String name, User user, Boolean isDefault) {
        this.name      = name;
        this.user      = user;
        this.isDefault = isDefault;
    }

    // ── Getters & Setters ─────────────────────────────
    public Long getId()                        { return id; }
    public void setId(Long id)                 { this.id = id; }

    public String getName()                    { return name; }
    public void setName(String name)           { this.name = name; }

    public User getUser()                      { return user; }
    public void setUser(User user)             { this.user = user; }

    public Boolean getIsDefault()              { return isDefault; }
    public void setIsDefault(Boolean val)      { this.isDefault = val; }

    @Override
    public String toString() {
        return "Category{id=" + id + ", name='" + name + "', isDefault=" + isDefault + "}";
    }
}