package com.budget.app.repository;

import com.budget.app.model.SavingsGoal;
import com.budget.app.model.SavingsGoalStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface SavingsGoalRepository extends JpaRepository<SavingsGoal, Long> {

    /**
     * Find all savings goals for a user
     * @param userId the user ID
     * @return list of savings goals
     */
    List<SavingsGoal> findByUserId(Long userId);

    /**
     * Find all savings goals for a user with a specific status
     * @param userId the user ID
     * @param status the savings goal status
     * @return list of savings goals matching the status
     */
    List<SavingsGoal> findByUserIdAndStatus(Long userId, SavingsGoalStatus status);

    /**
     * Find all active (IN_PROGRESS) savings goals for a user
     * @param userId the user ID
     * @return list of in-progress savings goals
     */
    List<SavingsGoal> findByUserIdAndStatusOrderByTargetDateAsc(Long userId, SavingsGoalStatus status);

    /**
     * Find all completed savings goals for a user
     * @param userId the user ID
     * @return list of completed savings goals
     */
    List<SavingsGoal> findByUserIdAndStatus(Long userId, String status);

    /**
     * Find savings goals by target date range
     * @param userId the user ID
     * @param startDate the start date
     * @param endDate the end date
     * @return list of savings goals within the date range
     */
    List<SavingsGoal> findByUserIdAndTargetDateBetween(Long userId, LocalDate startDate, LocalDate endDate);

    /**
     * Count total savings goals for a user
     * @param userId the user ID
     * @return count of savings goals
     */
    Long countByUserId(Long userId);

    /**
     * Check if a savings goal exists for a user
     * @param id the savings goal ID
     * @param userId the user ID
     * @return true if exists, false otherwise
     */
    boolean existsByIdAndUserId(Long id, Long userId);

    /**
     * Find a specific savings goal by ID and user ID
     * @param id the savings goal ID
     * @param userId the user ID
     * @return Optional containing the savings goal if found
     */
    Optional<SavingsGoal> findByIdAndUserId(Long id, Long userId);
}
