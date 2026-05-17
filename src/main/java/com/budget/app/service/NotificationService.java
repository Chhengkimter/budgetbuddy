package com.budget.app.service;

import com.budget.app.model.Notification;
import com.budget.app.model.NotificationType;
import com.budget.app.model.User;
import com.budget.app.repository.NotificationRepository;
import com.budget.app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    // ── Get all notifications for a user ──────────────
    public List<Notification> getNotificationsByUser(Long userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    // ── Get unread notifications for a user ─────────
    public List<Notification> getUnreadNotificationsByUser(Long userId) {
        return notificationRepository.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(userId);
    }

    // ── Get read notifications for a user ────────────
    public List<Notification> getReadNotificationsByUser(Long userId) {
        return notificationRepository.findByUserIdAndIsReadTrueOrderByCreatedAtDesc(userId);
    }

    // ── Get notifications by type for a user ────────
    public List<Notification> getNotificationsByType(Long userId, NotificationType type) {
        return notificationRepository.findByUserIdAndTypeOrderByCreatedAtDesc(userId, type);
    }

    // ── Get unread notifications by type ─────────────
    public List<Notification> getUnreadNotificationsByType(Long userId, NotificationType type) {
        return notificationRepository.findByUserIdAndTypeAndIsReadFalseOrderByCreatedAtDesc(userId, type);
    }

    // ── Get notifications by date range ──────────────
    public List<Notification> getNotificationsByDateRange(Long userId, LocalDateTime startDate, LocalDateTime endDate) {
        return notificationRepository.findByUserIdAndCreatedAtBetweenOrderByCreatedAtDesc(userId, startDate, endDate);
    }

    // ── Get a single notification by ID ──────────────
    public Optional<Notification> getNotificationById(Long id) {
        return notificationRepository.findById(id);
    }

    // ── Get notification by ID and user ID ──────────
    public Optional<Notification> getNotificationByIdAndUser(Long id, Long userId) {
        return notificationRepository.findByIdAndUserId(id, userId);
    }

    // ── Create a new notification ────────────────────
    public Notification createNotification(Long userId, Notification notification) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        notification.setUser(user);
        notification.setIsRead(false);
        notification.setCreatedAt(LocalDateTime.now());

        return notificationRepository.save(notification);
    }

    // ── Create notification with type ───────────────
    public Notification createNotification(Long userId, String title, String message, NotificationType type) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        Notification notification = new Notification(title, message, type, user);
        return notificationRepository.save(notification);
    }

    // ── Create general notification ──────────────────
    public Notification createGeneralNotification(Long userId, String title, String message) {
        return createNotification(userId, title, message, NotificationType.GENERAL);
    }

    // ── Mark notification as read ────────────────────
    public Notification markAsRead(Long id) {
        Notification notification = notificationRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Notification not found with id: " + id));

        notification.markAsRead();
        return notificationRepository.save(notification);
    }

    // ── Mark notification as unread ──────────────────
    public Notification markAsUnread(Long id) {
        Notification notification = notificationRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Notification not found with id: " + id));

        notification.markAsUnread();
        return notificationRepository.save(notification);
    }

    // ── Toggle read status ───────────────────────────
    public Notification toggleReadStatus(Long id) {
        Notification notification = notificationRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Notification not found with id: " + id));

        notification.toggleReadStatus();
        return notificationRepository.save(notification);
    }

    // ── Mark all notifications as read ───────────────
    public void markAllAsRead(Long userId) {
        List<Notification> unreadNotifications = notificationRepository.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(userId);
        for (Notification notification : unreadNotifications) {
            notification.markAsRead();
        }
        notificationRepository.saveAll(unreadNotifications);
    }

    // ── Update notification ──────────────────────────
    public Notification updateNotification(Long id, Notification updatedNotification) {
        Notification existing = notificationRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Notification not found with id: " + id));

        existing.setTitle(updatedNotification.getTitle());
        existing.setMessage(updatedNotification.getMessage());
        existing.setType(updatedNotification.getType());

        return notificationRepository.save(existing);
    }

    // ── Delete notification ──────────────────────────
    public void deleteNotification(Long id) {
        if (!notificationRepository.existsById(id)) {
            throw new RuntimeException("Notification not found with id: " + id);
        }
        notificationRepository.deleteById(id);
    }

    // ── Delete all read notifications for user ──────
    public void deleteAllReadNotifications(Long userId) {
        notificationRepository.deleteByUserIdAndIsReadTrue(userId);
    }

    // ── Delete old notifications ─────────────────────
    public Long deleteOldNotifications(Long userId, LocalDateTime beforeDate) {
        return notificationRepository.deleteByUserIdAndCreatedAtBefore(userId, beforeDate);
    }

    // ── Count unread notifications ───────────────────
    public Long countUnreadNotifications(Long userId) {
        return notificationRepository.countByUserIdAndIsReadFalse(userId);
    }

    // ── Count total notifications ────────────────────
    public Long countTotalNotifications(Long userId) {
        return notificationRepository.countByUserId(userId);
    }

    // ── Count notifications by type ──────────────────
    public Long countNotificationsByType(Long userId, NotificationType type) {
        return notificationRepository.countByUserIdAndType(userId, type);
    }

    // ── Get latest unread notification ───────────────
    public Optional<Notification> getLatestUnreadNotification(Long userId) {
        return notificationRepository.findLatestUnreadNotification(userId);
    }

    // ── Check if user owns notification ──────────────
    public boolean isUserOwner(Long id, Long userId) {
        return notificationRepository.existsByIdAndUserId(id, userId);
    }
}
