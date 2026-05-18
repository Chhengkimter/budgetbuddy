package com.budget.app.repository;

import com.budget.app.model.AuditLog;
import com.budget.app.model.AuditAction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    /**
     * Find all audit logs for a specific table
     * @param tableName the table name
     * @return list of audit logs
     */
    List<AuditLog> findByTableNameOrderByCreatedAtDesc(String tableName);

    /**
     * Find all audit logs for a specific record
     * @param tableName the table name
     * @param recordId the record ID
     * @return list of audit logs for that record
     */
    List<AuditLog> findByTableNameAndRecordIdOrderByCreatedAtDesc(String tableName, Long recordId);

    /**
     * Find audit logs by action type
     * @param action the audit action (CREATE, UPDATE, DELETE)
     * @return list of audit logs
     */
    List<AuditLog> findByActionOrderByCreatedAtDesc(AuditAction action);

    /**
     * Find audit logs by user
     * @param userId the user ID
     * @return list of audit logs
     */
    @Query("SELECT al FROM AuditLog al WHERE al.user.id = :userId ORDER BY al.createdAt DESC")
    List<AuditLog> findByUserId(@Param("userId") Long userId);

    /**
     * Find audit logs by user and action
     * @param userId the user ID
     * @param action the audit action
     * @return list of audit logs
     */
    @Query("SELECT al FROM AuditLog al WHERE al.user.id = :userId AND al.action = :action ORDER BY al.createdAt DESC")
    List<AuditLog> findByUserIdAndAction(@Param("userId") Long userId, @Param("action") AuditAction action);

    /**
     * Find audit logs by user and table name
     * @param userId the user ID
     * @param tableName the table name
     * @return list of audit logs
     */
    @Query("SELECT al FROM AuditLog al WHERE al.user.id = :userId AND al.tableName = :tableName ORDER BY al.createdAt DESC")
    List<AuditLog> findByUserIdAndTableName(@Param("userId") Long userId, @Param("tableName") String tableName);

    /**
     * Find audit logs within a date range
     * @param startDate the start date
     * @param endDate the end date
     * @return list of audit logs within the date range
     */
    List<AuditLog> findByCreatedAtBetweenOrderByCreatedAtDesc(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Find audit logs for a user within a date range
     * @param userId the user ID
     * @param startDate the start date
     * @param endDate the end date
     * @return list of audit logs
     */
    @Query("SELECT al FROM AuditLog al WHERE al.user.id = :userId AND al.createdAt BETWEEN :startDate AND :endDate ORDER BY al.createdAt DESC")
    List<AuditLog> findByUserIdAndDateRange(@Param("userId") Long userId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    /**
     * Find audit logs for a specific table and action
     * @param tableName the table name
     * @param action the audit action
     * @return list of audit logs
     */
    List<AuditLog> findByTableNameAndActionOrderByCreatedAtDesc(String tableName, AuditAction action);

    /**
     * Find audit history for a specific record
     * @param tableName the table name
     * @param recordId the record ID
     * @param action the audit action
     * @return list of audit logs
     */
    List<AuditLog> findByTableNameAndRecordIdAndActionOrderByCreatedAtDesc(String tableName, Long recordId, AuditAction action);

    /**
     * Count audit logs for a table
     * @param tableName the table name
     * @return count of audit logs
     */
    Long countByTableName(String tableName);

    /**
     * Count audit logs for a user
     * @param userId the user ID
     * @return count of audit logs
     */
    @Query("SELECT COUNT(al) FROM AuditLog al WHERE al.user.id = :userId")
    Long countByUserId(@Param("userId") Long userId);

    /**
     * Count audit logs by action type
     * @param action the audit action
     * @return count of audit logs
     */
    Long countByAction(AuditAction action);

    /**
     * Delete audit logs older than a specified date
     * @param beforeDate the cutoff date
     * @return count of deleted records
     */
    Long deleteByCreatedAtBefore(LocalDateTime beforeDate);

    /**
     * Delete audit logs for a specific table older than a date
     * @param tableName the table name
     * @param beforeDate the cutoff date
     * @return count of deleted records
     */
    Long deleteByTableNameAndCreatedAtBefore(String tableName, LocalDateTime beforeDate);
}
