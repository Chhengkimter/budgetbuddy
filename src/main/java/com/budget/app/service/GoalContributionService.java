package com.budget.app.service;

import com.budget.app.model.GoalContribution;
import com.budget.app.model.SavingsGoal;
import com.budget.app.repository.GoalContributionRepository;
import com.budget.app.repository.SavingsGoalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class GoalContributionService {

    @Autowired
    private GoalContributionRepository goalContributionRepository;

    @Autowired
    private SavingsGoalRepository savingsGoalRepository;

    // ── Get all contributions for a goal ──────────────
    public List<GoalContribution> getContributionsByGoal(Long goalId) {
        return goalContributionRepository.findByGoalIdOrderByContributedAtDesc(goalId);
    }

    // ── Get a single contribution by ID ───────────────
    public Optional<GoalContribution> getContributionById(Long id) {
        return goalContributionRepository.findById(id);
    }

    // ── Create a new contribution ────────────────────
    public GoalContribution createContribution(Long goalId, GoalContribution contribution) {
        SavingsGoal goal = savingsGoalRepository.findById(goalId)
            .orElseThrow(() -> new RuntimeException("Savings goal not found with id: " + goalId));
        
        contribution.setGoal(goal);
        contribution.setContributedAt(LocalDateTime.now());
        
        // Save contribution
        GoalContribution saved = goalContributionRepository.save(contribution);
        
        // Update goal's current amount
        goal.addSavings(contribution.getAmount());
        savingsGoalRepository.save(goal);
        
        return saved;
    }

    // ── Create contribution with just amount ─────────
    public GoalContribution createContribution(Long goalId, BigDecimal amount, String notes) {
        SavingsGoal goal = savingsGoalRepository.findById(goalId)
            .orElseThrow(() -> new RuntimeException("Savings goal not found with id: " + goalId));
        
        GoalContribution contribution = new GoalContribution(amount, notes, goal);
        
        // Save contribution
        GoalContribution saved = goalContributionRepository.save(contribution);
        
        // Update goal's current amount
        goal.addSavings(amount);
        savingsGoalRepository.save(goal);
        
        return saved;
    }

    // ── Update a contribution ────────────────────────
    public GoalContribution updateContribution(Long id, GoalContribution updatedContribution) {
        GoalContribution existing = goalContributionRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Contribution not found with id: " + id));
        
        BigDecimal oldAmount = existing.getAmount();
        BigDecimal newAmount = updatedContribution.getAmount();
        
        // Update contribution fields
        existing.setAmount(newAmount);
        existing.setNotes(updatedContribution.getNotes());
        
        // Save updated contribution
        GoalContribution saved = goalContributionRepository.save(existing);
        
        // Adjust goal's current amount if contribution amount changed
        if (oldAmount.compareTo(newAmount) != 0) {
            SavingsGoal goal = existing.getGoal();
            BigDecimal difference = newAmount.subtract(oldAmount);
            goal.setCurrentAmount(goal.getCurrentAmount().add(difference));
            goal.setUpdatedAt(LocalDateTime.now());
            savingsGoalRepository.save(goal);
        }
        
        return saved;
    }

    // ── Delete a contribution ────────────────────────
    public void deleteContribution(Long id) {
        GoalContribution contribution = goalContributionRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Contribution not found with id: " + id));
        
        // Get the goal and subtract the contribution amount
        SavingsGoal goal = contribution.getGoal();
        goal.setCurrentAmount(goal.getCurrentAmount().subtract(contribution.getAmount()));
        goal.setUpdatedAt(LocalDateTime.now());
        savingsGoalRepository.save(goal);
        
        // Delete contribution
        goalContributionRepository.deleteById(id);
    }

    // ── Get total contributions for a goal ────────────
    public BigDecimal getTotalContributions(Long goalId) {
        List<GoalContribution> contributions = goalContributionRepository.findByGoalId(goalId);
        return contributions.stream()
            .map(GoalContribution::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // ── Count contributions for a goal ───────────────
    public Long countContributions(Long goalId) {
        return goalContributionRepository.countByGoalId(goalId);
    }

    // ── Check if contribution exists ─────────────────
    public boolean contributionExists(Long id, Long goalId) {
        return goalContributionRepository.existsByIdAndGoalId(id, goalId);
    }
}
