package com.budget.app.repository;

import com.budget.app.model.GoalContribution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface GoalContributionRepository extends JpaRepository<GoalContribution, Long> {

    /**
     * Find all contributions for a specific savings goal
     * @param goalId the savings goal ID
     * @return list of contributions
     */
    List<GoalContribution> findByGoal_GoalID(Long goalId);

    /**
     * Find all contributions for a goal ordered by date descending
     * @param goalId the savings goal ID
     * @return list of contributions ordered by contributed_at DESC
     */
    List<GoalContribution> findByGoal_GoalIDOrderByContributedAtDesc(Long goalId);

    /**
     * Find contributions for a goal within a date range
     * @param goalId the savings goal ID
     * @param startDate the start date
     * @param endDate the end date
     * @return list of contributions within the date range
     */
    List<GoalContribution> findByGoal_GoalIDAndContributedAtBetween(Long goalId, LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Count total contributions for a goal
     * @param goalId the savings goal ID
     * @return count of contributions
     */
    Long countByGoal_GoalID(Long goalId);

    /**
     * Check if a contribution exists
     * @param id the contribution ID
     * @param goalId the savings goal ID
     * @return true if exists, false otherwise
     */
    boolean existsByIdAndGoal_GoalID(Long id, Long goalId);

}
