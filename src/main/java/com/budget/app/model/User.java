package com.budget.app.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "User")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "UserID")
    private Long userID;

    @Column(name = "UserFirstName", nullable = false, length = 100)
    private String userFirstName;

    @Column(name = "UserLastName", nullable = false, length = 100)
    private String userLastName;

    @Column(name = "UserEmail", nullable = false, unique = true, length = 150)
    private String userEmail;

    @Column(name = "UserPhoneNumber", length = 20)
    private String userPhoneNumber;

    @Column(name = "UserPassword", nullable = false)
    private String userPassword;

    @Column(name = "UserCreated")
    private LocalDateTime userCreated;

    @Column(name = "UserIsActive")
    private Boolean userIsActive;

    // ─── Lifecycle ────────────────────────────────────────────────────────────

    @PrePersist
    public void prePersist() {
        this.userCreated = LocalDateTime.now();
        this.userIsActive = true;
    }

    // ─── Getters & Setters ────────────────────────────────────────────────────

    public Long getUserID() { return userID; }
    public void setUserID(Long userID) { this.userID = userID; }

    public String getUserFirstName() { return userFirstName; }
    public void setUserFirstName(String userFirstName) { this.userFirstName = userFirstName; }

    public String getUserLastName() { return userLastName; }
    public void setUserLastName(String userLastName) { this.userLastName = userLastName; }

    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    public String getUserPhoneNumber() { return userPhoneNumber; }
    public void setUserPhoneNumber(String userPhoneNumber) { this.userPhoneNumber = userPhoneNumber; }

    public String getUserPassword() { return userPassword; }
    public void setUserPassword(String userPassword) { this.userPassword = userPassword; }

    public LocalDateTime getUserCreated() { return userCreated; }
    public void setUserCreated(LocalDateTime userCreated) { this.userCreated = userCreated; }

    public Boolean getUserIsActive() { return userIsActive; }
    public void setUserIsActive(Boolean userIsActive) { this.userIsActive = userIsActive; }
}