package com.macrotrack.goal.repository;

import com.macrotrack.goal.model.Goal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface GoalRepository extends JpaRepository<Goal, Long> {
    Optional<Goal> findByUserIdAndActiveTrue(Long userId);

    @Transactional
    @Modifying
    @Query("UPDATE Goal g SET g.active = false WHERE g.user.id = :userId AND g.active = true")
    int deactivateActiveGoals(@Param("userId") Long userId);
    
    List<Goal> findByUserIdOrderByStartDateDesc(Long userId);
}