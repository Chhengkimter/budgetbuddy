package com.budget.app.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is required")
    @Column(nullable = false)
    private String name;

    @Email(message = "Must be a valid email")
    @NotBlank(message = "Email is required")
    @Column(nullable = false, unique = true)
    private String email;

    @NotBlank(message = "Password is required")
    @Column(nullable = false)
    private String password;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Budget> budgets;

    public User() {}

    public User(String name, String email, String password) {
        this.name     = name;
        this.email    = email;
        this.password = password;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.isActive  = true;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId()                        { return id; }
    public void setId(Long id)                 { this.id = id; }

    public String getName()                    { return name; }
    public void setName(String name)           { this.name = name; }

    public String getEmail()                   { return email; }
    public void setEmail(String email)         { this.email = email; }

    public String getPassword()                { return password; }
    public void setPassword(String pw)         { this.password = pw; }

    public LocalDateTime getCreatedAt()        { return createdAt; }
    public void setCreatedAt(LocalDateTime dt) { this.createdAt = dt; }

    public LocalDateTime getUpdatedAt()        { return updatedAt; }
    public void setUpdatedAt(LocalDateTime dt) { this.updatedAt = dt; }

    public Boolean getIsActive()               { return isActive; }
    public void setIsActive(Boolean isActive)  { this.isActive = isActive; }

    public List<Budget> getBudgets()           { return budgets; }
    public void setBudgets(List<Budget> b)     { this.budgets = b; }

    @Override
    public String toString() {
        return "User{id=" + id + ", name='" + name + "', email='" + email + "', isActive=" + isActive + "}";
    }
}
