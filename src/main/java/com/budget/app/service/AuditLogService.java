package com.budget.app.service;

import com.budget.app.model.AuditLog;
import com.budget.app.model.AuditAction;
import com.budget.app.model.User;
import com.budget.app.repository.AuditLogRepository;
import com.budget.app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AuditLogService {

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Autowired
    private UserRepository userRepository;

    // ── Create audit log ──────────────────────────────
    /**
     * Create a new audit log entry
     * @param action the audit action (CREATE, UPDATE, DELETE)
     * @param tableName the affected table name
     * @param recordId the affected record ID
     * @return the created audit log
     */
    public AuditLog createAuditLog(AuditAction action, String tableName, Long recordId) {
        AuditLog auditLog = new AuditLog(action, tableName, recordId);
        return auditLogRepository.save(auditLog);
    }

    /**
     * Create a new audit log entry with old and new values
     * @param action the audit action (CREATE, UPDATE, DELETE)
     * @param tableName the affected table name
     * @param recordId the affected record ID
     * @param oldValue the old value (JSON string)
     * @param newValue the new value (JSON string)
     * @return the created audit log
     */
    public AuditLog createAuditLog(AuditAction action, String tableName, Long recordId, String oldValue, String newValue) {
        AuditLog auditLog = new AuditLog(action, tableName, recordId, oldValue, newValue);
        return auditLogRepository.save(auditLog);
    }

    /**
     * Create a new audit log entry for a specific user
     * @param action the audit action
     * @param tableName the affected table name
     * @param recordId the affected record ID
     * @param userId the user performing the action
     * @return the created audit log
     */
    public AuditLog createAuditLog(AuditAction action, String tableName, Long recordId, Long userId) {
        User user = null;
        if (userId != null) {
            user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        }
        AuditLog auditLog = new AuditLog(action, tableName, recordId);
        auditLog.setUser(user);
        return auditLogRepository.save(auditLog);
    }

    /**
     * Create a new audit log entry for a specific user with old and new values
     * @param action the audit action
     * @param tableName the affected table name
     * @param recordId the affected record ID
     * @param oldValue the old value (JSON string)
     * @param newValue the new value (JSON string)
     * @param userId the user performing the action
     * @return the created audit log
     */
    public AuditLog createAuditLog(AuditAction action, String tableName, Long recordId, String oldValue, String newValue, Long userId) {
        User user = null;
        if (userId != null) {
            user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        }
        AuditLog auditLog = new AuditLog(action, tableName, recordId, oldValue, newValue, user);
        return auditLogRepository.save(auditLog);
    }

    // ── Get audit logs ────────────────────────────────
    /**
     * Get a single audit log by ID
     * @param id the audit log ID
     * @return optional containing the audit log
     */
    public Optional<AuditLog> getAuditLogById(Long id) {
        return auditLogRepository.findById(id);
    }

    /**
     * Get all audit logs for a specific table
     * @param tableName the table name
     * @return list of audit logs
     */
    public List<AuditLog> getAuditLogsByTable(String tableName) {
        return auditLogRepository.findByTableNameOrderByCreatedAtDesc(tableName);
    }

    /**
     * Get all audit logs for a specific record
     * @param tableName the table name
     * @param recordId the record ID
     * @return list of audit logs for that record
     */
    public List<AuditLog> getAuditLogsByRecord(String tableName, Long recordId) {
        return auditLogRepository.findByTableNameAndRecordIdOrderByCreatedAtDesc(tableName, recordId);
    }

    /**
     * Get all audit logs by action type
     * @param action the audit action (CREATE, UPDATE, DELETE)
     * @return list of audit logs
     */
    public List<AuditLog> getAuditLogsByAction(AuditAction action) {
        return auditLogRepository.findByActionOrderByCreatedAtDesc(action);
    }

    /**
     * Get all audit logs for a specific user
     * @param userId the user ID
     * @return list of audit logs
     */
    public List<AuditLog> getAuditLogsByUser(Long userId) {
        return auditLogRepository.findByUserId(userId);
    }

    /**
     * Get audit logs by user and action
     * @param userId the user ID
     * @param action the audit action
     * @return list of audit logs
     */
    public List<AuditLog> getAuditLogsByUserAndAction(Long userId, AuditAction action) {
        return auditLogRepository.findByUserIdAndAction(userId, action);
    }

    /**
     * Get audit logs by user and table name
     * @param userId the user ID
     * @param tableName the table name
     * @return list of audit logs
     */
    public List<AuditLog> getAuditLogsByUserAndTable(Long userId, String tableName) {
        return auditLogRepository.findByUserIdAndTableName(userId, tableName);
    }

    /**
     * Get audit logs within a date range
     * @param startDate the start date
     * @param endDate the end date
     * @return list of audit logs within the date range
     */
    public List<AuditLog> getAuditLogsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return auditLogRepository.findByCreatedAtBetweenOrderByCreatedAtDesc(startDate, endDate);
    }

    /**
     * Get audit logs for a user within a date range
     * @param userId the user ID
     * @param startDate the start date
     * @param endDate the end date
     * @return list of audit logs
     */
    public List<AuditLog> getAuditLogsByUserAndDateRange(Long userId, LocalDateTime startDate, LocalDateTime endDate) {
        return auditLogRepository.findByUserIdAndDateRange(userId, startDate, endDate);
    }

    /**
     * Get audit logs by table and action
     * @param tableName the table name
     * @param action the audit action
     * @return list of audit logs
     */
    public List<AuditLog> getAuditLogsByTableAndAction(String tableName, AuditAction action) {
        return auditLogRepository.findByTableNameAndActionOrderByCreatedAtDesc(tableName, action);
    }

    /**
     * Get audit history for a specific record and action
     * @param tableName the table name
     * @param recordId the record ID
     * @param action the audit action
     * @return list of audit logs
     */
    public List<AuditLog> getAuditHistoryByRecordAndAction(String tableName, Long recordId, AuditAction action) {
        return auditLogRepository.findByTableNameAndRecordIdAndActionOrderByCreatedAtDesc(tableName, recordId, action);
    }

    // ── Get all audit logs ────────────────────────────
    /**
     * Get all audit logs (with ordering)
     * @return all audit logs
     */
    public List<AuditLog> getAllAuditLogs() {
        return auditLogRepository.findAll();
    }

    // ── Statistics and counts ─────────────────────────
    /**
     * Count total audit logs for a table
     * @param tableName the table name
     * @return count of audit logs
     */
    public Long countAuditLogsByTable(String tableName) {
        return auditLogRepository.countByTableName(tableName);
    }

    /**
     * Count total audit logs for a user
     * @param userId the user ID
     * @return count of audit logs
     */
    public Long countAuditLogsByUser(Long userId) {
        return auditLogRepository.countByUserId(userId);
    }

    /**
     * Count audit logs by action type
     * @param action the audit action
     * @return count of audit logs
     */
    public Long countAuditLogsByAction(AuditAction action) {
        return auditLogRepository.countByAction(action);
    }

    /**
     * Get total count of all audit logs
     * @return total count
     */
    public Long getTotalAuditLogCount() {
        return auditLogRepository.count();
    }

    // ── Cleanup operations ────────────────────────────
    /**
     * Delete audit logs older than a specified date
     * @param beforeDate the cutoff date
     * @return count of deleted records
     */
    public Long deleteOldAuditLogs(LocalDateTime beforeDate) {
        return auditLogRepository.deleteByCreatedAtBefore(beforeDate);
    }

    /**
     * Delete audit logs for a specific table older than a date
     * @param tableName the table name
     * @param beforeDate the cutoff date
     * @return count of deleted records
     */
    public Long deleteOldAuditLogsByTable(String tableName, LocalDateTime beforeDate) {
        return auditLogRepository.deleteByTableNameAndCreatedAtBefore(tableName, beforeDate);
    }

    /**
     * Delete a single audit log by ID
     * @param id the audit log ID
     */
    public void deleteAuditLog(Long id) {
        if (!auditLogRepository.existsById(id)) {
            throw new RuntimeException("Audit log not found with id: " + id);
        }
        auditLogRepository.deleteById(id);
    }

    /**
     * Delete all audit logs for a specific table
     * @param tableName the table name
     */
    public void deleteAllAuditLogsByTable(String tableName) {
        List<AuditLog> logs = auditLogRepository.findByTableNameOrderByCreatedAtDesc(tableName);
        auditLogRepository.deleteAll(logs);
    }

    /**
     * Delete all audit logs for a specific record
     * @param tableName the table name
     * @param recordId the record ID
     */
    public void deleteAllAuditLogsByRecord(String tableName, Long recordId) {
        List<AuditLog> logs = auditLogRepository.findByTableNameAndRecordIdOrderByCreatedAtDesc(tableName, recordId);
        auditLogRepository.deleteAll(logs);
    }

    /**
     * Delete all audit logs (use with caution!)
     */
    public void deleteAllAuditLogs() {
        auditLogRepository.deleteAll();
    }
}
