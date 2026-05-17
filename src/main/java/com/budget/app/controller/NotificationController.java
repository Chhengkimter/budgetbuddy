package com.budget.app.controller;

import com.budget.app.model.Notification;
import com.budget.app.model.NotificationType;
import com.budget.app.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "*")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    // ── GET /api/notifications/user/{userId} ────────────────
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Notification>> getNotificationsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(notificationService.getNotificationsByUser(userId));
    }

    // ── GET /api/notifications/user/{userId}/unread ─────────
    @GetMapping("/user/{userId}/unread")
    public ResponseEntity<List<Notification>> getUnreadNotificationsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(notificationService.getUnreadNotificationsByUser(userId));
    }

    // ── GET /api/notifications/user/{userId}/read ───────────
    @GetMapping("/user/{userId}/read")
    public ResponseEntity<List<Notification>> getReadNotificationsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(notificationService.getReadNotificationsByUser(userId));
    }

    // ── GET /api/notifications/user/{userId}/type/{type} ────
    @GetMapping("/user/{userId}/type/{type}")
    public ResponseEntity<?> getNotificationsByType(@PathVariable Long userId, @PathVariable String type) {
        try {
            NotificationType notificationType = NotificationType.valueOf(type.toUpperCase());
            return ResponseEntity.ok(notificationService.getNotificationsByType(userId, notificationType));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid notification type: " + type));
        }
    }

    // ── GET /api/notifications/user/{userId}/type/{type}/unread ──
    @GetMapping("/user/{userId}/type/{type}/unread")
    public ResponseEntity<?> getUnreadNotificationsByType(@PathVariable Long userId, @PathVariable String type) {
        try {
            NotificationType notificationType = NotificationType.valueOf(type.toUpperCase());
            return ResponseEntity.ok(notificationService.getUnreadNotificationsByType(userId, notificationType));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid notification type: " + type));
        }
    }

    // ── GET /api/notifications/{id} ──────────────────────────
    @GetMapping("/{id}")
    public ResponseEntity<Notification> getNotificationById(@PathVariable Long id) {
        return notificationService.getNotificationById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    // ── POST /api/notifications/user/{userId} ────────────────
    @PostMapping("/user/{userId}")
    public ResponseEntity<?> createNotification(@PathVariable Long userId,
                                                 @Valid @RequestBody Notification notification) {
        try {
            Notification created = notificationService.createNotification(userId, notification);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ── POST /api/notifications/user/{userId}/quick ──────────
    @PostMapping("/user/{userId}/quick")
    public ResponseEntity<?> createQuickNotification(@PathVariable Long userId,
                                                      @RequestBody Map<String, String> request) {
        try {
            String title = request.get("title");
            String message = request.get("message");
            String typeStr = request.get("type");

            if (title == null || title.isBlank()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Title is required"));
            }
            if (message == null || message.isBlank()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Message is required"));
            }

            NotificationType type = typeStr != null ? NotificationType.valueOf(typeStr.toUpperCase()) : NotificationType.GENERAL;
            Notification created = notificationService.createNotification(userId, title, message, type);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid notification type"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ── PUT /api/notifications/{id} ──────────────────────────
    @PutMapping("/{id}")
    public ResponseEntity<?> updateNotification(@PathVariable Long id,
                                                 @Valid @RequestBody Notification notification) {
        try {
            return ResponseEntity.ok(notificationService.updateNotification(id, notification));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ── PUT /api/notifications/{id}/mark-read ────────────────
    @PutMapping("/{id}/mark-read")
    public ResponseEntity<?> markAsRead(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(notificationService.markAsRead(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ── PUT /api/notifications/{id}/mark-unread ──────────────
    @PutMapping("/{id}/mark-unread")
    public ResponseEntity<?> markAsUnread(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(notificationService.markAsUnread(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ── PUT /api/notifications/{id}/toggle ───────────────────
    @PutMapping("/{id}/toggle")
    public ResponseEntity<?> toggleReadStatus(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(notificationService.toggleReadStatus(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ── PUT /api/notifications/user/{userId}/mark-all-read ───
    @PutMapping("/user/{userId}/mark-all-read")
    public ResponseEntity<?> markAllAsRead(@PathVariable Long userId) {
        try {
            notificationService.markAllAsRead(userId);
            return ResponseEntity.ok(Map.of("message", "All notifications marked as read"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ── DELETE /api/notifications/{id} ───────────────────────
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteNotification(@PathVariable Long id) {
        try {
            notificationService.deleteNotification(id);
            return ResponseEntity.ok(Map.of("message", "Notification deleted successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ── DELETE /api/notifications/user/{userId}/read ────────
    @DeleteMapping("/user/{userId}/read")
    public ResponseEntity<?> deleteAllReadNotifications(@PathVariable Long userId) {
        try {
            notificationService.deleteAllReadNotifications(userId);
            return ResponseEntity.ok(Map.of("message", "All read notifications deleted successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ── DELETE /api/notifications/user/{userId}/old ──────────
    @DeleteMapping("/user/{userId}/old")
    public ResponseEntity<?> deleteOldNotifications(@PathVariable Long userId,
                                                     @RequestParam(defaultValue = "30") Integer days) {
        try {
            LocalDateTime beforeDate = LocalDateTime.now().minusDays(days);
            Long deletedCount = notificationService.deleteOldNotifications(userId, beforeDate);
            return ResponseEntity.ok(Map.of("message", "Deleted old notifications", "count", deletedCount));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ── GET /api/notifications/user/{userId}/count ───────────
    @GetMapping("/user/{userId}/count")
    public ResponseEntity<Map<String, Long>> countNotifications(@PathVariable Long userId) {
        Long total = notificationService.countTotalNotifications(userId);
        Long unread = notificationService.countUnreadNotifications(userId);
        return ResponseEntity.ok(Map.of("total", total, "unread", unread));
    }

    // ── GET /api/notifications/user/{userId}/count/type/{type} ─
    @GetMapping("/user/{userId}/count/type/{type}")
    public ResponseEntity<?> countNotificationsByType(@PathVariable Long userId, @PathVariable String type) {
        try {
            NotificationType notificationType = NotificationType.valueOf(type.toUpperCase());
            Long count = notificationService.countNotificationsByType(userId, notificationType);
            return ResponseEntity.ok(Map.of("type", type, "count", count));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid notification type: " + type));
        }
    }

    // ── GET /api/notifications/user/{userId}/latest-unread ───
    @GetMapping("/user/{userId}/latest-unread")
    public ResponseEntity<?> getLatestUnreadNotification(@PathVariable Long userId) {
        return notificationService.getLatestUnreadNotification(userId)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.noContent().build());
    }

    // ── GET /api/notifications/user/{userId}/unread/count ────
    @GetMapping("/user/{userId}/unread/count")
    public ResponseEntity<Map<String, Long>> getUnreadCount(@PathVariable Long userId) {
        Long unreadCount = notificationService.countUnreadNotifications(userId);
        return ResponseEntity.ok(Map.of("unreadCount", unreadCount));
    }
}
