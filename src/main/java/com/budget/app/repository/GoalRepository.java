package com.budget.app.repository;

import com.budget.app.model.Goal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GoalRepository extends JpaRepository<Goal, Long> {

    /** All goals belonging to a user, ordered newest first. */
    List<Goal> findByUserIDOrderByGoalCreatedDateDesc(Long userID);

    /** Find a single goal owned by a specific user (prevents cross-user access). */
    Optional<Goal> findByGoalIDAndUserID(Long goalID, Long userID);

    /** Goals that are not yet finished (null GoalFinishedDate). */
    @Query("SELECT g FROM Goal g WHERE g.userID = :userID AND g.goalFinishedDate IS NULL " +
           "ORDER BY g.goalCreatedDate DESC")
    List<Goal> findActiveGoalsByUser(@Param("userID") Long userID);

    /** Count of active (unfinished) goals for a user. */
    @Query("SELECT COUNT(g) FROM Goal g WHERE g.userID = :userID AND g.goalFinishedDate IS NULL")
    long countActiveGoalsByUser(@Param("userID") Long userID);
}