package com.budget.app.controller;

import com.budget.app.model.AuditLog;
import com.budget.app.model.AuditAction;
import com.budget.app.service.AuditLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/audit-logs")
@CrossOrigin(origins = "*")
public class AuditLogController {

    @Autowired
    private AuditLogService auditLogService;

    // GET /api/audit-logs/{id}
    /**
     * Get a single audit log by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<AuditLog> getAuditLogById(@PathVariable Long id) {
        return auditLogService.getAuditLogById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    // GET /api/audit-logs/table/{tableName}
    /**
     * Get all audit logs for a specific table
     */
    @GetMapping("/table/{tableName}")
    public ResponseEntity<List<AuditLog>> getAuditLogsByTable(@PathVariable String tableName) {
        return ResponseEntity.ok(auditLogService.getAuditLogsByTable(tableName));
    }

    // GET /api/audit-logs/table/{tableName}/record/{recordId}
    /**
     * Get all audit logs for a specific record
     */
    @GetMapping("/table/{tableName}/record/{recordId}")
    public ResponseEntity<List<AuditLog>> getAuditLogsByRecord(
            @PathVariable String tableName,
            @PathVariable Long recordId) {
        return ResponseEntity.ok(auditLogService.getAuditLogsByRecord(tableName, recordId));
    }

    // GET /api/audit-logs/action/{action}
    /**
     * Get all audit logs by action type (CREATE, UPDATE, DELETE)
     */
    @GetMapping("/action/{action}")
    public ResponseEntity<?> getAuditLogsByAction(@PathVariable String action) {
        try {
            AuditAction auditAction = AuditAction.valueOf(action.toUpperCase());
            return ResponseEntity.ok(auditLogService.getAuditLogsByAction(auditAction));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid action: " + action));
        }
    }

    // GET /api/audit-logs/user/{userId}
    /**
     * Get all audit logs for a specific user
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AuditLog>> getAuditLogsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(auditLogService.getAuditLogsByUser(userId));
    }

    // GET /api/audit-logs/user/{userId}/action/{action}
    /**
     * Get audit logs by user and action
     */
    @GetMapping("/user/{userId}/action/{action}")
    public ResponseEntity<?> getAuditLogsByUserAndAction(
            @PathVariable Long userId,
            @PathVariable String action) {
        try {
            AuditAction auditAction = AuditAction.valueOf(action.toUpperCase());
            return ResponseEntity.ok(auditLogService.getAuditLogsByUserAndAction(userId, auditAction));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid action: " + action));
        }
    }

    // GET /api/audit-logs/user/{userId}/table/{tableName}
    /**
     * Get audit logs by user and table name
     */
    @GetMapping("/user/{userId}/table/{tableName}")
    public ResponseEntity<List<AuditLog>> getAuditLogsByUserAndTable(
            @PathVariable Long userId,
            @PathVariable String tableName) {
        return ResponseEntity.ok(auditLogService.getAuditLogsByUserAndTable(userId, tableName));
    }

    // GET /api/audit-logs/table/{tableName}/action/{action}
    /**
     * Get audit logs by table and action
     */
    @GetMapping("/table/{tableName}/action/{action}")
    public ResponseEntity<?> getAuditLogsByTableAndAction(
            @PathVariable String tableName,
            @PathVariable String action) {
        try {
            AuditAction auditAction = AuditAction.valueOf(action.toUpperCase());
            return ResponseEntity.ok(auditLogService.getAuditLogsByTableAndAction(tableName, auditAction));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid action: " + action));
        }
    }

    // GET /api/audit-logs/date-range
    /**
     * Get audit logs within a date range
     * Query params: startDate (ISO datetime), endDate (ISO datetime)
     * Example: /api/audit-logs/date-range?startDate=2025-01-01T00:00:00&endDate=2025-12-31T23:59:59
     */
    @GetMapping("/date-range")
    public ResponseEntity<?> getAuditLogsByDateRange(
            @RequestParam LocalDateTime startDate,
            @RequestParam LocalDateTime endDate) {
        if (startDate.isAfter(endDate)) {
            return ResponseEntity.badRequest().body(Map.of("error", "Start date must be before end date"));
        }
        return ResponseEntity.ok(auditLogService.getAuditLogsByDateRange(startDate, endDate));
    }

    // GET /api/audit-logs/user/{userId}/date-range
    /**
     * Get audit logs for a user within a date range
     * Query params: startDate (ISO datetime), endDate (ISO datetime)
     */
    @GetMapping("/user/{userId}/date-range")
    public ResponseEntity<?> getAuditLogsByUserAndDateRange(
            @PathVariable Long userId,
            @RequestParam LocalDateTime startDate,
            @RequestParam LocalDateTime endDate) {
        if (startDate.isAfter(endDate)) {
            return ResponseEntity.badRequest().body(Map.of("error", "Start date must be before end date"));
        }
        return ResponseEntity.ok(auditLogService.getAuditLogsByUserAndDateRange(userId, startDate, endDate));
    }

    // GET /api/audit-logs/all
    /**
     * Get all audit logs (use with caution on large datasets)
     */
    @GetMapping("/all")
    public ResponseEntity<List<AuditLog>> getAllAuditLogs() {
        return ResponseEntity.ok(auditLogService.getAllAuditLogs());
    }

    // GET /api/audit-logs/stats/count/table/{tableName}
    /**
     * Get count of audit logs for a specific table
     */
    @GetMapping("/stats/count/table/{tableName}")
    public ResponseEntity<Map<String, Object>> countAuditLogsByTable(@PathVariable String tableName) {
        Long count = auditLogService.countAuditLogsByTable(tableName);
        return ResponseEntity.ok(Map.of(
            "tableName", tableName,
            "count", count
        ));
    }

    // GET /api/audit-logs/stats/count/user/{userId}
    /**
     * Get count of audit logs for a specific user
     */
    @GetMapping("/stats/count/user/{userId}")
    public ResponseEntity<Map<String, Object>> countAuditLogsByUser(@PathVariable Long userId) {
        Long count = auditLogService.countAuditLogsByUser(userId);
        return ResponseEntity.ok(Map.of(
            "userId", userId,
            "count", count
        ));
    }

    // GET /api/audit-logs/stats/count/action/{action}
    /**
     * Get count of audit logs by action type
     */
    @GetMapping("/stats/count/action/{action}")
    public ResponseEntity<?> countAuditLogsByAction(@PathVariable String action) {
        try {
            AuditAction auditAction = AuditAction.valueOf(action.toUpperCase());
            Long count = auditLogService.countAuditLogsByAction(auditAction);
            return ResponseEntity.ok(Map.of(
                "action", action,
                "count", count
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid action: " + action));
        }
    }

    // GET /api/audit-logs/stats/total
    /**
     * Get total count of all audit logs
     */
    @GetMapping("/stats/total")
    public ResponseEntity<Map<String, Object>> getTotalAuditLogCount() {
        Long count = auditLogService.getTotalAuditLogCount();
        return ResponseEntity.ok(Map.of("total", count));
    }

    // DELETE /api/audit-logs/{id}
    /**
     * Delete a single audit log by ID
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAuditLog(@PathVariable Long id) {
        try {
            auditLogService.deleteAuditLog(id);
            return ResponseEntity.ok(Map.of("message", "Audit log deleted successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // DELETE /api/audit-logs/table/{tableName}
    /**
     * Delete all audit logs for a specific table
     */
    @DeleteMapping("/table/{tableName}")
    public ResponseEntity<?> deleteAllAuditLogsByTable(@PathVariable String tableName) {
        try {
            auditLogService.deleteAllAuditLogsByTable(tableName);
            return ResponseEntity.ok(Map.of("message", "All audit logs for table deleted successfully", "table", tableName));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // DELETE /api/audit-logs/table/{tableName}/record/{recordId}
    /**
     * Delete all audit logs for a specific record
     */
    @DeleteMapping("/table/{tableName}/record/{recordId}")
    public ResponseEntity<?> deleteAllAuditLogsByRecord(
            @PathVariable String tableName,
            @PathVariable Long recordId) {
        try {
            auditLogService.deleteAllAuditLogsByRecord(tableName, recordId);
            return ResponseEntity.ok(Map.of(
                "message", "All audit logs for record deleted successfully",
                "table", tableName,
                "recordId", recordId
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // DELETE /api/audit-logs/cleanup/old
    /**
     * Delete audit logs older than a specified date
     * Query param: beforeDate (ISO datetime)
     * Example: /api/audit-logs/cleanup/old?beforeDate=2024-01-01T00:00:00
     */
    @DeleteMapping("/cleanup/old")
    public ResponseEntity<?> deleteOldAuditLogs(@RequestParam LocalDateTime beforeDate) {
        try {
            Long deletedCount = auditLogService.deleteOldAuditLogs(beforeDate);
            return ResponseEntity.ok(Map.of(
                "message", "Old audit logs deleted successfully",
                "deletedCount", deletedCount,
                "beforeDate", beforeDate
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // DELETE /api/audit-logs/cleanup/table/{tableName}
    /**
     * Delete audit logs for a specific table older than a date
     * Query param: beforeDate (ISO datetime)
     */
    @DeleteMapping("/cleanup/table/{tableName}")
    public ResponseEntity<?> deleteOldAuditLogsByTable(
            @PathVariable String tableName,
            @RequestParam LocalDateTime beforeDate) {
        try {
            Long deletedCount = auditLogService.deleteOldAuditLogsByTable(tableName, beforeDate);
            return ResponseEntity.ok(Map.of(
                "message", "Old audit logs for table deleted successfully",
                "table", tableName,
                "deletedCount", deletedCount,
                "beforeDate", beforeDate
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // DELETE /api/audit-logs/cleanup/all
    /**
     * Delete all audit logs (use with extreme caution!)
     */
    @DeleteMapping("/cleanup/all")
    public ResponseEntity<?> deleteAllAuditLogs() {
        try {
            auditLogService.deleteAllAuditLogs();
            return ResponseEntity.ok(Map.of("message", "All audit logs deleted successfully (WARNING: This action cannot be undone)"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
