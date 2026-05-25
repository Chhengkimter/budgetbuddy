package com.budget.app.service;

import com.budget.app.model.GoalContribution;
import com.budget.app.model.Goal;
import com.budget.app.repository.GoalContributionRepository;
import com.budget.app.repository.GoalRepository;
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
    private GoalRepository goalRepository;

    // ── Get all contributions for a goal ──────────────
    public List<GoalContribution> getContributionsByGoal(Long goalId) {
        return goalContributionRepository.findByGoal_GoalIDOrderByContributedAtDesc(goalId);
    }

    // ── Get a single contribution by ID ───────────────
    public Optional<GoalContribution> getContributionById(Long id) {
        return goalContributionRepository.findById(id);
    }

    // ── Create a new contribution ────────────────────
    public GoalContribution createContribution(Long goalId, GoalContribution contribution) {
        Goal goal = goalRepository.findById(goalId)
            .orElseThrow(() -> new RuntimeException("Goal not found with id: " + goalId));

        contribution.setGoal(goal);
        contribution.setContributedAt(LocalDateTime.now());

        // Save contribution
        return goalContributionRepository.save(contribution);
    }

    // ── Create contribution with just amount ─────────
    public GoalContribution createContribution(Long goalId, BigDecimal amount, String notes) {
        Goal goal = goalRepository.findById(goalId)
            .orElseThrow(() -> new RuntimeException("Goal not found with id: " + goalId));

        GoalContribution contribution = new GoalContribution(amount, notes, goal);

        // Save contribution
        return goalContributionRepository.save(contribution);
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
        
        // Save updated contribution (no Goal aggregate fields to update in Goal entity)
        return goalContributionRepository.save(existing);
    }

    // ── Delete a contribution ────────────────────────
    public void deleteContribution(Long id) {
        GoalContribution contribution = goalContributionRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Contribution not found with id: " + id));
        // Delete contribution (no Goal aggregate fields to update)
        goalContributionRepository.deleteById(id);
    }

    // ── Get total contributions for a goal ────────────
    public BigDecimal getTotalContributions(Long goalId) {
        List<GoalContribution> contributions = goalContributionRepository.findByGoal_GoalID(goalId);
        return contributions.stream()
            .map(GoalContribution::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // ── Count contributions for a goal ───────────────
    public Long countContributions(Long goalId) {
        return goalContributionRepository.countByGoal_GoalID(goalId);
    }

    // ── Check if contribution exists ─────────────────
    public boolean contributionExists(Long id, Long goalId) {
        return goalContributionRepository.existsByIdAndGoal_GoalID(id, goalId);
    }
}
