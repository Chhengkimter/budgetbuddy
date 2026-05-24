package com.budget.app.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "Notification")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "NotificationID")
    private Long notificationID;

    @Column(name = "UserID", nullable = false)
    private Long userID;

    /** e.g. "BUDGET_EXCEEDED", "GOAL_REACHED", "RECURRING_GENERATED" */
    @Column(name = "NotificationType")
    private String notificationType;

    @Column(name = "NotificationTitle")
    private String notificationTitle;

    @Column(name = "NotificationMessage")
    private String notificationMessage;

    @Column(name = "IsRead")
    private Boolean isRead = false;

    @Column(name = "CreatedAt")
    private LocalDateTime createdAt;

    /** ID of the related Budget / Goal / Transaction (polymorphic reference). */
    @Column(name = "ReferenceID")
    private Long referenceID;

    public Notification() {}

    public Notification(Long userID, String type, String title, String message, Long referenceID) {
        this.userID              = userID;
        this.notificationType    = type;
        this.notificationTitle   = title;
        this.notificationMessage = message;
        this.referenceID         = referenceID;
        this.isRead              = false;
        this.createdAt           = LocalDateTime.now();
    }

    public Long          getNotificationID()                          { return notificationID; }
    public void          setNotificationID(Long notificationID)       { this.notificationID = notificationID; }

    public Long          getUserID()                                  { return userID; }
    public void          setUserID(Long userID)                       { this.userID = userID; }

    public String        getNotificationType()                        { return notificationType; }
    public void          setNotificationType(String type)             { this.notificationType = type; }

    public String        getNotificationTitle()                       { return notificationTitle; }
    public void          setNotificationTitle(String title)           { this.notificationTitle = title; }

    public String        getNotificationMessage()                     { return notificationMessage; }
    public void          setNotificationMessage(String message)       { this.notificationMessage = message; }

    public Boolean       getIsRead()                                  { return isRead; }
    public void          setIsRead(Boolean isRead)                    { this.isRead = isRead; }

    public LocalDateTime getCreatedAt()                               { return createdAt; }
    public void          setCreatedAt(LocalDateTime createdAt)        { this.createdAt = createdAt; }

    public Long          getReferenceID()                             { return referenceID; }
    public void          setReferenceID(Long referenceID)             { this.referenceID = referenceID; }
}