package com.macrotrack.weight.repository;

import com.macrotrack.weight.model.WeightEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface WeightEntryRepository extends JpaRepository<WeightEntry, Long> {
    List<WeightEntry> findByUserIdAndDateBetweenOrderByDateAsc(Long userId, LocalDate from, LocalDate to);
    
    Page<WeightEntry> findAllByUserIdAndDateBetweenOrderByDateAsc(Long userId, LocalDate from, LocalDate to, Pageable pageable);

    Optional<WeightEntry> findByUserIdAndDate(Long userId, LocalDate date);

    Optional<WeightEntry> findFirstByUserIdOrderByDateAsc(Long userId);

    Optional<WeightEntry> findTopByUserIdOrderByDateDesc(Long userId);

    @Query("SELECT COUNT(DISTINCT w.date) FROM WeightEntry w WHERE w.user.id = :userId AND w.date BETWEEN :from AND :to AND EXISTS (SELECT 1 FROM FoodLog f WHERE f.user.id = :userId AND f.date = w.date)")
    long countDaysWithBothLogs(@Param("userId") Long userId, @Param("from") LocalDate from, @Param("to") LocalDate to);

    @org.springframework.data.jpa.repository.Modifying
    @Query("DELETE FROM WeightEntry w WHERE w.id = :id AND w.user.id = :userId")
    int deleteByIdAndUserId(@Param("id") Long id, @Param("userId") Long userId);
}