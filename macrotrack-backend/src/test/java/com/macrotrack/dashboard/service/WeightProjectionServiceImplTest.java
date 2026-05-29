package com.macrotrack.dashboard.service;

import com.macrotrack.analytics.adherence.AdherenceMetrics;
import com.macrotrack.analytics.adherence.AdherenceService;
import com.macrotrack.analytics.bmr.BmrCalculatorService;
import com.macrotrack.analytics.common.DateValuePoint;
import com.macrotrack.analytics.projection.MultiScenarioProjectionResponse;
import com.macrotrack.analytics.projection.ProjectionResponse;
import com.macrotrack.analytics.projection.WeightForecastResult;
import com.macrotrack.analytics.projection.WeightProjectionEngine;
import com.macrotrack.analytics.projection.WeightSmoothingService;
import com.macrotrack.analytics.tdee.TdeeAlgorithmService;
import com.macrotrack.analytics.tdee.TdeeInternalQueryPort;
import com.macrotrack.goal.dto.GoalResponse;
import com.macrotrack.goal.model.MetabolicGoalType;
import com.macrotrack.goal.service.GoalInternalQueryPort;
import com.macrotrack.user.model.User;
import com.macrotrack.user.service.UserInternalQueryPort;
import com.macrotrack.weight.model.WeightEntry;
import com.macrotrack.weight.service.WeightEntryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WeightProjectionServiceImplTest {

    @Mock private UserInternalQueryPort userService;
    @Mock private GoalInternalQueryPort goalService;
    @Mock private TdeeAlgorithmService tdeeAlgorithmService;
    @Mock private WeightSmoothingService weightSmoothingService;
    @Mock private TdeeInternalQueryPort tdeeEstimationService;
    @Mock private WeightEntryService weightEntryService;
    @Mock private WeightProjectionEngine weightProjectionEngine;
    @Mock private BmrCalculatorService bmrCalculatorService;
    @Mock private AdherenceService adherenceService;
    @Mock private Clock clock;

    @InjectMocks
    private WeightProjectionServiceImpl weightProjectionService;

    private User testUser;
    private LocalDate testDate;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@test.com");
        testDate = LocalDate.of(2026, 1, 10);
    }

    @Test
    void getProjection_noActiveGoal_returnsEmpty() {
        when(clock.instant()).thenReturn(Instant.parse("2026-01-10T00:00:00Z"));
        when(clock.getZone()).thenReturn(ZoneId.of("UTC"));
        when(userService.getUserEntityByEmail("test@test.com")).thenReturn(testUser);
        when(weightEntryService.getEntriesBetween(eq("test@test.com"), any(LocalDate.class), any(LocalDate.class))).thenReturn(List.of());
        when(weightSmoothingService.computeWeightTrend(any(List.class))).thenReturn(new java.util.TreeMap<>());
        when(goalService.findActiveGoal(testUser)).thenReturn(Optional.empty());

        ProjectionResponse response = weightProjectionService.getProjection("test@test.com");

        assertNotNull(response);
        assertTrue(response.points().isEmpty());
        assertNull(response.targetReachedDate());
        assertFalse(response.extremeDeficit());
    }

    @Test
    void getProjection_withGoal_returnsProjection() {
        when(clock.instant()).thenReturn(Instant.parse("2026-01-10T00:00:00Z"));
        when(clock.getZone()).thenReturn(ZoneId.of("UTC"));
        when(userService.getUserEntityByEmail("test@test.com")).thenReturn(testUser);

        WeightEntry entry = new WeightEntry();
        entry.setWeightKg(80.0);
        List<WeightEntry> entries = List.of(entry);
        when(weightEntryService.getEntriesBetween(eq("test@test.com"), any(LocalDate.class), any(LocalDate.class))).thenReturn(entries);

        java.util.NavigableMap<LocalDate, Double> trend = new java.util.TreeMap<>(Map.of(testDate, 80.0));
        when(weightSmoothingService.computeWeightTrend(any(List.class))).thenReturn(trend);

        GoalResponse activeGoal = new GoalResponse(
                1L, 75.0, -0.5, testDate.minusDays(5), true
        );
        when(goalService.findActiveGoal(testUser)).thenReturn(Optional.of(activeGoal));

        when(tdeeEstimationService.effectiveTdee(testUser, testDate)).thenReturn(2500.0);
        when(weightEntryService.resolveInitialWeight(testUser, activeGoal, 80.0)).thenReturn(80.0);
        when(weightEntryService.getLatestBodyFatPercentage(testUser)).thenReturn(20.0);
        when(bmrCalculatorService.computeInitialTdee(testUser, 80.0, 20.0)).thenReturn(2300.0);

        List<DateValuePoint<Double>> points = List.of(new DateValuePoint<>(testDate.plusDays(10), 75.0));
        WeightForecastResult result = new WeightForecastResult(points, testDate.plusDays(10), MetabolicGoalType.LOSS, false);
        
        when(weightProjectionEngine.generateWeightProjection(any())).thenReturn(result);

        ProjectionResponse response = weightProjectionService.getProjection("test@test.com");

        assertNotNull(response);
        assertEquals(1, response.points().size());
        assertEquals(testDate.plusDays(10), response.targetReachedDate());
        assertFalse(response.extremeDeficit());
    }

    @Test
    void getProjection_noWeightData_throwsException() {
        when(clock.instant()).thenReturn(Instant.parse("2026-01-10T00:00:00Z"));
        when(clock.getZone()).thenReturn(ZoneId.of("UTC"));
        when(userService.getUserEntityByEmail("test@test.com")).thenReturn(testUser);
        when(weightEntryService.getEntriesBetween(eq("test@test.com"), any(LocalDate.class), any(LocalDate.class))).thenReturn(List.of());
        when(weightSmoothingService.computeWeightTrend(any(List.class))).thenReturn(new java.util.TreeMap<>());
        
        GoalResponse activeGoal = new GoalResponse(
                1L, 75.0, -0.5, testDate, true
        );
        when(goalService.findActiveGoal(testUser)).thenReturn(Optional.of(activeGoal));
        when(tdeeEstimationService.effectiveTdee(testUser, testDate)).thenReturn(2500.0);
        when(weightEntryService.getFirstWeightKg(testUser)).thenReturn(null);
        when(weightEntryService.resolveInitialWeight(testUser, activeGoal, null)).thenReturn(null);

        assertThrows(ResponseStatusException.class, () -> {
            weightProjectionService.getProjection("test@test.com");
        });
    }

    @Test
    void getMultiScenarioProjection_withEnoughData_returnsIdealAndEmpirical() {
        when(clock.instant()).thenReturn(Instant.parse("2026-01-10T00:00:00Z"));
        when(clock.getZone()).thenReturn(ZoneId.of("UTC"));
        when(userService.getUserEntityByEmail("test@test.com")).thenReturn(testUser);
        
        WeightEntry entry = new WeightEntry();
        entry.setWeightKg(80.0);
        when(weightEntryService.getEntriesBetween(eq("test@test.com"), any(LocalDate.class), any(LocalDate.class))).thenReturn(List.of(entry));
        when(weightSmoothingService.computeWeightTrend(any(List.class))).thenReturn(new java.util.TreeMap<>(Map.of(testDate, 80.0)));
        
        // start date is 6 days ago -> >= 5 days -> hasEnoughDataForEmpirical is true
        GoalResponse activeGoal = new GoalResponse(
                1L, 75.0, -0.5, testDate.minusDays(6), true
        );
        when(goalService.findActiveGoal(testUser)).thenReturn(Optional.of(activeGoal));
        when(tdeeEstimationService.effectiveTdee(testUser, testDate)).thenReturn(2500.0);
        when(weightEntryService.resolveInitialWeight(testUser, activeGoal, 80.0)).thenReturn(80.0);
        when(weightEntryService.getLatestBodyFatPercentage(testUser)).thenReturn(20.0);
        when(bmrCalculatorService.computeInitialTdee(testUser, 80.0, 20.0)).thenReturn(2300.0);
        
        // Ideal projection
        List<DateValuePoint<Double>> idealPoints = List.of(new DateValuePoint<>(testDate.plusDays(10), 75.0));
        WeightForecastResult idealResult = new WeightForecastResult(idealPoints, testDate.plusDays(10), MetabolicGoalType.LOSS, false);
        
        // empirical
        when(adherenceService.computeAdherence(any(), any(), any(Double.class), any())).thenReturn(AdherenceMetrics.insufficient());
        when(adherenceService.computeAverageCalories(testUser.getId(), activeGoal, testDate)).thenReturn(2000.0); // 500 deficit
        // deficit 500 kcal -> -500 * 7 / 7700 = -0.454 kg / week
        
        when(weightProjectionEngine.generateWeightProjection(any())).thenReturn(idealResult); // return same result for simplicity

        MultiScenarioProjectionResponse response = weightProjectionService.getMultiScenarioProjection("test@test.com");

        assertNotNull(response);
        assertTrue(response.hasEnoughDataForEmpirical());
        assertEquals(1, response.idealPoints().size());
        assertEquals(1, response.empiricalPoints().size());
    }
}
