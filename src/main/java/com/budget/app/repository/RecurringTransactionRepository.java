package com.budget.app.repository;

import com.budget.app.model.RecurringTransaction;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface RecurringTransactionRepository extends JpaRepository<RecurringTransaction, Long> {

    // ── Used by RecurringTransactionService ───────────────────────────────────

    /** All templates belonging to a user, active and inactive. */
    List<RecurringTransaction> findByUserIDOrderByRecurringIDDesc(Long userID);

    /** Ownership check — prevents cross-user access. */
    Optional<RecurringTransaction> findByRecurringIDAndUserID(Long recurringID, Long userID);

    // ── Used by the scheduler (RecurringTransactionScheduler) ────────────────

    /**
     * All active templates whose recurringDay matches today's day-of-month
     * and whose start date is on or before today.
     *
     * The scheduler calls this daily to find templates that should fire today.
     * It then checks rtLastGeneratedDate to avoid double-generation within the
     * same month.
     */
    @Query("SELECT r FROM RecurringTransaction r " +
           "WHERE r.rtIsActive = true " +
           "AND r.recurringDay = :dayOfMonth " +
           "AND r.rtStartDate <= :today " +
           "AND (r.rtEndDate IS NULL OR r.rtEndDate >= :today)")
    List<RecurringTransaction> findTemplatesToFireToday(@Param("dayOfMonth") int       dayOfMonth,
                                                         @Param("today")      LocalDate today);

    /**
     * All active SAVING templates for a user.
     * Used by the Goals overview to list active monthly saving commitments.
     */
    @Query("SELECT r FROM RecurringTransaction r " +
           "WHERE r.userID = :userID " +
           "AND r.rTransactionType = 'SAVING' " +
           "AND r.rtIsActive = true " +
           "ORDER BY r.recurringID DESC")
    List<RecurringTransaction> findActiveSavingTemplatesByUser(@Param("userID") Long userID);
}