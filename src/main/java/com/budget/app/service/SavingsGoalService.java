package com.budget.app.service;

import com.budget.app.model.SavingsGoal;
import com.budget.app.model.SavingsGoalStatus;
import com.budget.app.model.User;
import com.budget.app.repository.SavingsGoalRepository;
import com.budget.app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class SavingsGoalService {

    @Autowired
    private SavingsGoalRepository savingsGoalRepository;

    @Autowired
    private UserRepository userRepository;

    // ── Get savings goals ─────────────────────────────
    /**
     * Get all savings goals for a user
     * @param userId the user ID
     * @return list of savings goals
     */
    public List<SavingsGoal> getSavingsGoalsByUser(Long userId) {
        return savingsGoalRepository.findByUserId(userId);
    }

    /**
     * Get a single savings goal by ID
     * @param id the savings goal ID
     * @return optional containing the savings goal
     */
    public Optional<SavingsGoal> getSavingsGoalById(Long id) {
        return savingsGoalRepository.findById(id);
    }

    /**
     * Get a specific savings goal for a user
     * @param id the savings goal ID
     * @param userId the user ID
     * @return optional containing the savings goal if user owns it
     */
    public Optional<SavingsGoal> getSavingsGoalByIdAndUser(Long id, Long userId) {
        return savingsGoalRepository.findByIdAndUserId(id, userId);
    }

    /**
     * Get all savings goals for a user with a specific status
     * @param userId the user ID
     * @param status the savings goal status
     * @return list of savings goals with the specified status
     */
    public List<SavingsGoal> getSavingsGoalsByUserAndStatus(Long userId, SavingsGoalStatus status) {
        return savingsGoalRepository.findByUserIdAndStatus(userId, status);
    }

    /**
     * Get all active (IN_PROGRESS) savings goals for a user, ordered by target date
     * @param userId the user ID
     * @return list of active savings goals
     */
    public List<SavingsGoal> getActiveSavingsGoals(Long userId) {
        return savingsGoalRepository.findByUserIdAndStatusOrderByTargetDateAsc(userId, SavingsGoalStatus.IN_PROGRESS);
    }

    /**
     * Get all completed savings goals for a user
     * @param userId the user ID
     * @return list of completed savings goals
     */
    public List<SavingsGoal> getCompletedSavingsGoals(Long userId) {
        return savingsGoalRepository.findByUserIdAndStatus(userId, SavingsGoalStatus.COMPLETED);
    }

    /**
     * Get all cancelled savings goals for a user
     * @param userId the user ID
     * @return list of cancelled savings goals
     */
    public List<SavingsGoal> getCancelledSavingsGoals(Long userId) {
        return savingsGoalRepository.findByUserIdAndStatus(userId, SavingsGoalStatus.CANCELLED);
    }

    /**
     * Get savings goals within a target date range
     * @param userId the user ID
     * @param startDate the start date
     * @param endDate the end date
     * @return list of savings goals with target date in the range
     */
    public List<SavingsGoal> getSavingsGoalsByDateRange(Long userId, LocalDate startDate, LocalDate endDate) {
        return savingsGoalRepository.findByUserIdAndTargetDateBetween(userId, startDate, endDate);
    }

    // ── Create savings goal ───────────────────────────
    /**
     * Create a new savings goal
     * @param userId the user ID
     * @param savingsGoal the savings goal to create
     * @return the created savings goal
     */
    public SavingsGoal createSavingsGoal(Long userId, SavingsGoal savingsGoal) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        savingsGoal.setUserId(userId);
        savingsGoal.setCurrentAmount(BigDecimal.ZERO);
        savingsGoal.setStatus(SavingsGoalStatus.IN_PROGRESS);
        savingsGoal.setCreatedAt(LocalDateTime.now());
        savingsGoal.setUpdatedAt(LocalDateTime.now());
        return savingsGoalRepository.save(savingsGoal);
    }

    // ── Update savings goal ───────────────────────────
    /**
     * Update a savings goal
     * @param id the savings goal ID
     * @param updatedGoal the updated savings goal data
     * @return the updated savings goal
     */
    public SavingsGoal updateSavingsGoal(Long id, SavingsGoal updatedGoal) {
        SavingsGoal existing = savingsGoalRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Savings goal not found with id: " + id));
        
        if (updatedGoal.getName() != null) {
            existing.setName(updatedGoal.getName());
        }
        if (updatedGoal.getDescription() != null) {
            existing.setDescription(updatedGoal.getDescription());
        }
        if (updatedGoal.getTargetAmount() != null) {
            existing.setTargetAmount(updatedGoal.getTargetAmount());
        }
        if (updatedGoal.getTargetDate() != null) {
            existing.setTargetDate(updatedGoal.getTargetDate());
        }
        
        existing.setUpdatedAt(LocalDateTime.now());
        return savingsGoalRepository.save(existing);
    }

    /**
     * Update the target amount of a savings goal
     * @param id the savings goal ID
     * @param newTargetAmount the new target amount
     * @return the updated savings goal
     */
    public SavingsGoal updateTargetAmount(Long id, BigDecimal newTargetAmount) {
        SavingsGoal existing = savingsGoalRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Savings goal not found with id: " + id));
        existing.setTargetAmount(newTargetAmount);
        existing.setUpdatedAt(LocalDateTime.now());
        return savingsGoalRepository.save(existing);
    }

    /**
     * Update the status of a savings goal
     * @param id the savings goal ID
     * @param newStatus the new status
     * @return the updated savings goal
     */
    public SavingsGoal updateStatus(Long id, SavingsGoalStatus newStatus) {
        SavingsGoal existing = savingsGoalRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Savings goal not found with id: " + id));
        existing.setStatus(newStatus);
        existing.setUpdatedAt(LocalDateTime.now());
        return savingsGoalRepository.save(existing);
    }

    /**
     * Add contribution/savings to a goal
     * @param id the savings goal ID
     * @param amount the amount to add
     * @return the updated savings goal
     */
    public SavingsGoal addSavings(Long id, BigDecimal amount) {
        SavingsGoal existing = savingsGoalRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Savings goal not found with id: " + id));
        
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Amount must be greater than 0");
        }
        
        existing.addSavings(amount);
        existing.setUpdatedAt(LocalDateTime.now());
        return savingsGoalRepository.save(existing);
    }

    /**
     * Withdraw from a savings goal
     * @param id the savings goal ID
     * @param amount the amount to withdraw
     * @return the updated savings goal
     */
    public SavingsGoal withdrawSavings(Long id, BigDecimal amount) {
        SavingsGoal existing = savingsGoalRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Savings goal not found with id: " + id));
        
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Amount must be greater than 0");
        }
        
        if (existing.getCurrentAmount().compareTo(amount) < 0) {
            throw new RuntimeException("Cannot withdraw more than current amount");
        }
        
        existing.setCurrentAmount(existing.getCurrentAmount().subtract(amount));
        existing.setUpdatedAt(LocalDateTime.now());
        
        // Reset status if goal was completed but is no longer
        if (existing.getStatus() == SavingsGoalStatus.COMPLETED && !existing.isCompleted()) {
            existing.setStatus(SavingsGoalStatus.IN_PROGRESS);
        }
        
        return savingsGoalRepository.save(existing);
    }

    // ── Delete savings goal ───────────────────────────
    /**
     * Delete a savings goal
     * @param id the savings goal ID
     */
    public void deleteSavingsGoal(Long id) {
        if (!savingsGoalRepository.existsById(id)) {
            throw new RuntimeException("Savings goal not found with id: " + id);
        }
        savingsGoalRepository.deleteById(id);
    }

    // ── Statistics ────────────────────────────────────
    /**
     * Count total savings goals for a user
     * @param userId the user ID
     * @return count of savings goals
     */
    public Long countSavingsGoalsByUser(Long userId) {
        return savingsGoalRepository.countByUserId(userId);
    }

    /**
     * Get total saved amount across all active goals for a user
     * @param userId the user ID
     * @return total saved amount
     */
    public BigDecimal getTotalSavedAmount(Long userId) {
        return getSavingsGoalsByUser(userId).stream()
            .map(SavingsGoal::getCurrentAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Get total target amount across all active goals for a user
     * @param userId the user ID
     * @return total target amount
     */
    public BigDecimal getTotalTargetAmount(Long userId) {
        return getSavingsGoalsByUser(userId).stream()
            .map(SavingsGoal::getTargetAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Get total remaining amount across all active goals for a user
     * @param userId the user ID
     * @return total remaining amount
     */
    public BigDecimal getTotalRemainingAmount(Long userId) {
        return getSavingsGoalsByUser(userId).stream()
            .map(SavingsGoal::getRemainingAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Get count of completed goals for a user
     * @param userId the user ID
     * @return count of completed goals
     */
    public Long countCompletedGoals(Long userId) {
        return (long) getCompletedSavingsGoals(userId).size();
    }

    /**
     * Get count of active goals for a user
     * @param userId the user ID
     * @return count of active goals
     */
    public Long countActiveGoals(Long userId) {
        return (long) getActiveSavingsGoals(userId).size();
    }

    /**
     * Get average progress percentage across all goals
     * @param userId the user ID
     * @return average progress as percentage
     */
    public BigDecimal getAverageProgressPercentage(Long userId) {
        List<SavingsGoal> goals = getSavingsGoalsByUser(userId);
        if (goals.isEmpty()) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal totalProgress = goals.stream()
            .map(SavingsGoal::getProgressPercentage)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        return totalProgress.divide(new BigDecimal(goals.size()), 2, java.math.RoundingMode.HALF_UP);
    }

    /**
     * Check if a savings goal belongs to a user
     * @param goalId the savings goal ID
     * @param userId the user ID
     * @return true if goal belongs to user
     */
    public boolean goalBelongsToUser(Long goalId, Long userId) {
        return savingsGoalRepository.existsByIdAndUserId(goalId, userId);
    }
}
