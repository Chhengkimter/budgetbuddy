package com.budget.app.repository;

import com.budget.app.model.Notification;
import com.budget.app.model.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    /**
     * Find all notifications for a user
     * @param userId the user ID
     * @return list of notifications
     */
    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);

    /**
     * Find all unread notifications for a user
     * @param userId the user ID
     * @return list of unread notifications
     */
    List<Notification> findByUserIdAndIsReadFalseOrderByCreatedAtDesc(Long userId);

    /**
     * Find all read notifications for a user
     * @param userId the user ID
     * @return list of read notifications
     */
    List<Notification> findByUserIdAndIsReadTrueOrderByCreatedAtDesc(Long userId);

    /**
     * Find notifications of a specific type for a user
     * @param userId the user ID
     * @param type the notification type
     * @return list of notifications of that type
     */
    List<Notification> findByUserIdAndTypeOrderByCreatedAtDesc(Long userId, NotificationType type);

    /**
     * Find unread notifications of a specific type for a user
     * @param userId the user ID
     * @param type the notification type
     * @return list of unread notifications of that type
     */
    List<Notification> findByUserIdAndTypeAndIsReadFalseOrderByCreatedAtDesc(Long userId, NotificationType type);

    /**
     * Find notifications created within a date range
     * @param userId the user ID
     * @param startDate the start date
     * @param endDate the end date
     * @return list of notifications in that range
     */
    List<Notification> findByUserIdAndCreatedAtBetweenOrderByCreatedAtDesc(Long userId, LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Count unread notifications for a user
     * @param userId the user ID
     * @return count of unread notifications
     */
    Long countByUserIdAndIsReadFalse(Long userId);

    /**
     * Count total notifications for a user
     * @param userId the user ID
     * @return count of all notifications
     */
    Long countByUserId(Long userId);

    /**
     * Count notifications of a specific type for a user
     * @param userId the user ID
     * @param type the notification type
     * @return count of notifications of that type
     */
    Long countByUserIdAndType(Long userId, NotificationType type);

    /**
     * Check if a notification exists for a user
     * @param id the notification ID
     * @param userId the user ID
     * @return true if exists and belongs to user, false otherwise
     */
    boolean existsByIdAndUserId(Long id, Long userId);

    /**
     * Find a notification by ID and user ID
     * @param id the notification ID
     * @param userId the user ID
     * @return Optional containing the notification if found
     */
    Optional<Notification> findByIdAndUserId(Long id, Long userId);

    /**
     * Delete all notifications older than a specified date
     * @param userId the user ID
     * @param beforeDate the cutoff date
     * @return count of deleted notifications
     */
    Long deleteByUserIdAndCreatedAtBefore(Long userId, LocalDateTime beforeDate);

    /**
     * Delete all read notifications for a user
     * @param userId the user ID
     */
    void deleteByUserIdAndIsReadTrue(Long userId);

    /**
     * Get latest unread notification for a user
     * @param userId the user ID
     * @return Optional containing the most recent unread notification
     */
    @Query(value = "SELECT * FROM notifications WHERE user_id = :userId AND is_read = FALSE ORDER BY created_at DESC LIMIT 1", 
           nativeQuery = true)
    Optional<Notification> findLatestUnreadNotification(@Param("userId") Long userId);
}
