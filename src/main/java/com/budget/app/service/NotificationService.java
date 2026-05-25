package com.budget.app.service;

import com.budget.app.model.Notification;
import com.budget.app.model.NotificationType;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class NotificationService {

    public List<Notification> getNotificationsByUser(Long userId) {
        return Collections.emptyList();
    }

    public List<Notification> getUnreadNotificationsByUser(Long userId) {
        return Collections.emptyList();
    }

    public List<Notification> getReadNotificationsByUser(Long userId) {
        return Collections.emptyList();
    }

    public List<Notification> getNotificationsByType(Long userId, NotificationType notificationType) {
        return Collections.emptyList();
    }

    public List<Notification> getUnreadNotificationsByType(Long userId, NotificationType notificationType) {
        return Collections.emptyList();
    }

    public Optional<Notification> getNotificationById(Long id) {
        return Optional.empty();
    }

    public Notification createNotification(Long userId, Notification notification) {
        return notification;
    }

    public Notification createNotification(Long userId, String title, String message, NotificationType type) {
        Notification notification = new Notification(userId, type.name(), title, message, null);
        return notification;
    }

    public Notification updateNotification(Long id, Notification notification) {
        return notification;
    }

    public Notification markAsRead(Long id) {
        Notification notification = new Notification();
        notification.setNotificationID(id);
        notification.setIsRead(true);
        return notification;
    }

    public Notification markAsUnread(Long id) {
        Notification notification = new Notification();
        notification.setNotificationID(id);
        notification.setIsRead(false);
        return notification;
    }

    public Notification toggleReadStatus(Long id) {
        Notification notification = new Notification();
        notification.setNotificationID(id);
        notification.setIsRead(null);
        return notification;
    }

    public void markAllAsRead(Long userId) {
        // no-op placeholder
    }

    public void deleteNotification(Long id) {
        // no-op placeholder
    }

    public void deleteAllReadNotifications(Long userId) {
        // no-op placeholder
    }

    public Long deleteOldNotifications(Long userId, LocalDateTime beforeDate) {
        return 0L;
    }

    public Long countTotalNotifications(Long userId) {
        return 0L;
    }

    public Long countUnreadNotifications(Long userId) {
        return 0L;
    }

    public Long countNotificationsByType(Long userId, NotificationType notificationType) {
        return 0L;
    }

    public Optional<Notification> getLatestUnreadNotification(Long userId) {
        return Optional.empty();
    }
}
