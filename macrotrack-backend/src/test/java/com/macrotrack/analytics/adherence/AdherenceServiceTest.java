package com.macrotrack.analytics.adherence;

import com.macrotrack.analytics.adherence.AdherenceMetrics;
import com.macrotrack.goal.dto.GoalResponse;
import com.macrotrack.nutrition.service.NutritionAnalyticsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class AdherenceServiceTest {

    private AdherenceService adherenceService;
    private NutritionAnalyticsService nutritionAnalyticsService;

    private static final double DELTA = 0.01;

    @BeforeEach
    void setUp() {
        nutritionAnalyticsService = Mockito.mock(NutritionAnalyticsService.class);
        adherenceService = new AdherenceService(nutritionAnalyticsService);
    }

    @Test
    void computeAdherence_insufficientDays_returnsInsufficient() {
        LocalDate today = LocalDate.of(2026, 1, 10);
        GoalResponse goal = new GoalResponse(1L, 75.0, -0.5, today.minusDays(5), true);

        AdherenceMetrics metrics = adherenceService.computeAdherence(1L, goal, 2000.0, today);

        assertTrue(metrics.insufficientData());
        assertEquals(0.0, metrics.adherenceScore());
    }

    @Test
    void computeAdherence_perfectAdherence() {
        LocalDate today = LocalDate.of(2026, 1, 30);
        LocalDate startDate = today.minusDays(30);
        GoalResponse goal = new GoalResponse(1L, 75.0, -0.5, startDate, true);

        Map<LocalDate, Double> dailyCals = new LinkedHashMap<>();
        for (int i = 0; i <= 30; i++) {
            dailyCals.put(startDate.plusDays(i), 2000.0);
        }

        Mockito.when(nutritionAnalyticsService.aggregateDailyCalories(1L, startDate, today)).thenReturn(dailyCals);

        AdherenceMetrics metrics = adherenceService.computeAdherence(1L, goal, 2000.0, today);

        assertFalse(metrics.insufficientData());
        assertEquals(1.0, metrics.adherenceScore(), DELTA);
        assertEquals(1.0, metrics.adherence7d(), DELTA);
        assertEquals(1.0, metrics.adherence14d(), DELTA);
        assertEquals(1.0, metrics.adherence30d(), DELTA);
        assertEquals(1.0, metrics.loggedAdherence(), DELTA);
    }

    @Test
    void computeAdherence_zeroCalorieDays_countedAsAdherentIfWithinTolerance() {
        LocalDate today = LocalDate.of(2026, 1, 30);
        LocalDate startDate = today.minusDays(30);
        GoalResponse goal = new GoalResponse(1L, 75.0, -0.5, startDate, true);

        Map<LocalDate, Double> dailyCals = new LinkedHashMap<>();
        for (int i = 0; i <= 30; i++) {
            // A 0-calorie day. Will be adherent if target is 0 or within tolerance
            dailyCals.put(startDate.plusDays(i), 0.0); 
        }

        Mockito.when(nutritionAnalyticsService.aggregateDailyCalories(1L, startDate, today)).thenReturn(dailyCals);

        // Effective target is 0, so 0 cals is perfectly adherent
        AdherenceMetrics metrics = adherenceService.computeAdherence(1L, goal, 0.0, today);

        assertFalse(metrics.insufficientData());
        assertEquals(1.0, metrics.adherenceScore(), DELTA);
        assertEquals(1.0, metrics.loggedAdherence(), DELTA); // zero calorie is a logged day (e.g. fasting)
    }

    @Test
    void computeAdherence_missingDays_lowersLoggedAdherence() {
        LocalDate today = LocalDate.of(2026, 1, 30);
        LocalDate startDate = today.minusDays(30);
        GoalResponse goal = new GoalResponse(1L, 75.0, -0.5, startDate, true);

        Map<LocalDate, Double> dailyCals = new LinkedHashMap<>();
        // Only log 15 days out of 30
        for (int i = 0; i < 15; i++) {
            dailyCals.put(startDate.plusDays(i), 2000.0);
        }

        Mockito.when(nutritionAnalyticsService.aggregateDailyCalories(1L, startDate, today)).thenReturn(dailyCals);

        AdherenceMetrics metrics = adherenceService.computeAdherence(1L, goal, 2000.0, today);

        assertFalse(metrics.insufficientData());
        assertEquals(0.5, metrics.loggedAdherence(), 0.05);
        // The days that were logged were perfect, so adherence is technically 1.0 for the logged days
        // However, the score weight is based on 7, 14, 30 days.
        // Since we logged the FIRST 15 days (oldest), adherence7d is 0 (no logs), 8-14 has some, 15-30 has all.
        // So overall score will be lower than 1.0
        assertTrue(metrics.adherenceScore() < 1.0);
        assertEquals(0.0, metrics.adherence7d(), DELTA);
    }
}
