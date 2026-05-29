package com.macrotrack.nutrition.repository;

import com.macrotrack.nutrition.model.FoodLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import com.macrotrack.nutrition.dto.DailyCalorieAggregation;
import com.macrotrack.nutrition.dto.DailyMacroAggregation;

public interface FoodLogRepository extends JpaRepository<FoodLog, Long> {
    List<FoodLog> findByUserIdAndDateOrderByCreatedAtAsc(Long userId, LocalDate date);

    @Query("SELECT new com.macrotrack.nutrition.dto.DailyMacroAggregation(SUM(f.caloriesKcal), SUM(f.proteinG), SUM(f.fatG), SUM(f.carbsG)) FROM FoodLog f WHERE f.user.id = :userId AND f.date = :date")
    DailyMacroAggregation aggregateDailyMacros(@Param("userId") Long userId, @Param("date") LocalDate date);


    
    @Query("SELECT new com.macrotrack.nutrition.dto.DailyCalorieAggregation(f.date, SUM(f.caloriesKcal)) FROM FoodLog f WHERE f.user.id = :userId AND f.date BETWEEN :from AND :to GROUP BY f.date ORDER BY f.date ASC")
    List<DailyCalorieAggregation> aggregateDailyCaloriesBetween(@Param("userId") Long userId, @Param("from") LocalDate from, @Param("to") LocalDate to);

    Page<FoodLog> findAllByUserIdAndDateBetweenOrderByDateAsc(Long userId, LocalDate from, LocalDate to, Pageable pageable);

    @Transactional
    @Modifying
    @Query("DELETE FROM FoodLog f WHERE f.id = :id AND f.user.id = :userId")
    int deleteByIdAndUserId(@Param("id") Long id, @Param("userId") Long userId);
}