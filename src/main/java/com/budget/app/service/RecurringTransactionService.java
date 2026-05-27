package com.budget.app.service;

import com.budget.app.dto.RecurringTransactionRequestDTO;
import com.budget.app.dto.RecurringTransactionResponseDTO;
import com.budget.app.model.Budget;
import com.budget.app.model.Goal;
import com.budget.app.model.RecurringTransaction;
import com.budget.app.repository.BudgetRepository;
import com.budget.app.repository.GoalRepository;
import com.budget.app.repository.RecurringTransactionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RecurringTransactionService {

    private final RecurringTransactionRepository recurringRepo;
    private final BudgetRepository budgetRepo;
    private final GoalRepository goalRepo;

    public RecurringTransactionService(RecurringTransactionRepository recurringRepo,
                                       BudgetRepository budgetRepo,
                                       GoalRepository goalRepo) {
        this.recurringRepo = recurringRepo;
        this.budgetRepo    = budgetRepo;
        this.goalRepo      = goalRepo;
    }

    public List<RecurringTransactionResponseDTO> getAllByUser(Long userID) {
        return recurringRepo.findByUserIDOrderByRecurringIDDesc(userID)
                .stream().map(this::toResponseDTO).collect(Collectors.toList());
    }

    public RecurringTransactionResponseDTO getByID(Long id, Long userID) {
        return toResponseDTO(findOwned(id, userID));
    }

    public RecurringTransactionResponseDTO create(Long userID, RecurringTransactionRequestDTO req) {
        RecurringTransaction rt = new RecurringTransaction(
                userID,
                req.getBudgetID(),
                req.getGoalID(),
                req.getRTransactionType().toUpperCase(),
                req.getRTransactionName(),
                req.getRTransactionNote(),
                req.getRTransactionAmount(),
                req.getRecurringDay(),
                req.getRtStartDate(),
                req.getRtEndDate()
        );
        return toResponseDTO(recurringRepo.save(rt));
    }

    public RecurringTransactionResponseDTO update(Long id, Long userID, RecurringTransactionRequestDTO req) {
        RecurringTransaction rt = findOwned(id, userID);
        rt.setBudgetID(req.getBudgetID());
        rt.setGoalID(req.getGoalID());
        rt.setRTransactionType(req.getRTransactionType().toUpperCase());
        rt.setRTransactionName(req.getRTransactionName());
        rt.setRTransactionNote(req.getRTransactionNote());
        rt.setRTransactionAmount(req.getRTransactionAmount());
        rt.setRecurringDay(req.getRecurringDay());
        rt.setRtStartDate(req.getRtStartDate());
        rt.setRtEndDate(req.getRtEndDate());
        return toResponseDTO(recurringRepo.save(rt));
    }

    public RecurringTransactionResponseDTO deactivate(Long id, Long userID) {
        RecurringTransaction rt = findOwned(id, userID);
        rt.setRtIsActive(false);
        return toResponseDTO(recurringRepo.save(rt));
    }

    public void delete(Long id, Long userID) {
        recurringRepo.delete(findOwned(id, userID));
    }

    private RecurringTransaction findOwned(Long id, Long userID) {
        return recurringRepo.findByRecurringIDAndUserID(id, userID)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Recurring transaction not found"));
    }
    private RecurringTransactionResponseDTO toResponseDTO(RecurringTransaction rt) {
        String budgetName = null;
        if (rt.getBudgetID() != null) {
            budgetName = budgetRepo.findById(rt.getBudgetID())
                    .map(Budget::getBudgetName).orElse(null);
        }
        String goalName = null;
        if (rt.getGoalID() != null) {
            goalName = goalRepo.findById(rt.getGoalID())
                    .map(Goal::getGoalName).orElse(null);
        }

        RecurringTransactionResponseDTO dto = new RecurringTransactionResponseDTO();
        dto.setRecurringID(rt.getRecurringID());
        dto.setUserID(rt.getUserID());
        dto.setBudgetID(rt.getBudgetID());
        dto.setBudgetName(budgetName);
        dto.setGoalID(rt.getGoalID());
        dto.setGoalName(goalName);
        dto.setRTransactionType(rt.getRTransactionType());
        dto.setRTransactionName(rt.getRTransactionName());
        dto.setRTransactionNote(rt.getRTransactionNote());
        dto.setRTransactionAmount(rt.getRTransactionAmount());
        dto.setRecurringDay(rt.getRecurringDay());
        dto.setRtStartDate(rt.getRtStartDate());
        dto.setRtEndDate(rt.getRtEndDate());
        dto.setRtIsActive(rt.isRtIsActive());
        dto.setRtLastGeneratedDate(rt.getRtLastGeneratedDate());
        return dto;
    }
}