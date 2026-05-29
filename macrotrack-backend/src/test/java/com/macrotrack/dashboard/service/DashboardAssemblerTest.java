package com.macrotrack.dashboard.service;

import com.macrotrack.analytics.adherence.AdherenceMetrics;
import com.macrotrack.analytics.adherence.AdherenceService;
import com.macrotrack.analytics.adherence.DataConfidenceService;
import com.macrotrack.analytics.projection.WeightSmoothingService;
import com.macrotrack.dashboard.dto.DashboardResponse;
import com.macrotrack.goal.dto.GoalResponse;
import com.macrotrack.nutrition.dto.DailyNutritionResponse;
import com.macrotrack.nutrition.dto.MacroTargets;
import com.macrotrack.nutrition.service.NutritionAnalyticsService;
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

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DashboardAssemblerTest {

    @Mock
    private UserInternalQueryPort userService;
    @Mock
    private WeightEntryService weightEntryService;
    @Mock
    private NutritionAnalyticsService nutritionAnalyticsService;
    @Mock
    private DataConfidenceService dataConfidenceService;
    @Mock
    private AdherenceService adherenceService;
    @Mock
    private NutritionTargetService nutritionTargetService;
    @Mock
    private WeightSmoothingService weightSmoothingService;
    @Mock
    private Clock clock;

    @InjectMocks
    private DashboardAssembler dashboardAssembler;

    private User testUser;
    private LocalDate testDate;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@test.com");

        testDate = LocalDate.of(2026, 1, 1);
    }

    @Test
    void assembleDashboard_withFullData_returnsCompleteResponse() {
        when(userService.getUserEntityByEmail("test@test.com")).thenReturn(testUser);
        
        DailyNutritionResponse nutritionResponse = new DailyNutritionResponse(2000.0, 150.0, 60.0, 200.0);
        when(nutritionAnalyticsService.getDailyNutrition(testUser, testDate)).thenReturn(nutritionResponse);

        WeightEntry weightEntry = new WeightEntry();
        weightEntry.setWeightKg(80.0);
        List<WeightEntry> entries = List.of(weightEntry);
        when(weightEntryService.getEntriesBetweenForUser(eq(testUser), any(), eq(testDate))).thenReturn(entries);

        java.util.NavigableMap<LocalDate, Double> trend = new java.util.TreeMap<>(Map.of(testDate, 80.0));
        when(weightSmoothingService.computeWeightTrend(entries)).thenReturn(trend);

        GoalResponse activeGoalResponse = new GoalResponse(
                1L, 75.0, -0.5, testDate.minusDays(5), true
        );
        MacroTargets macroTargets = new MacroTargets(160.0, 70.0, 210.0);
        NutritionTargetResult targetResult = new NutritionTargetResult(
                2500.0, activeGoalResponse, 2200.0, macroTargets
        );
        when(nutritionTargetService.computeTargets(testUser, testDate, 80.0, entries, trend))
                .thenReturn(targetResult);

        when(dataConfidenceService.computeDataConfidenceScore(testUser, testDate)).thenReturn(85);

        AdherenceMetrics adherence = AdherenceMetrics.insufficient();
        when(adherenceService.computeAdherence(testUser.getId(), activeGoalResponse, 2200.0, testDate)).thenReturn(adherence);

        DashboardResponse response = dashboardAssembler.assembleDashboard("test@test.com", testDate);

        assertNotNull(response);
        assertEquals(2200.0, response.dailyCalorieTarget());
        assertEquals(2500.0, response.currentTdee());
        assertEquals(2000.0, response.todayCaloriesKcal());
        assertEquals(80.0, response.latestWeight());
        assertNotNull(response.activeGoal());
        assertEquals(160.0, response.macroTargets().proteinG());
        assertEquals(150.0, response.todayMacros().proteinG());
        assertEquals(85, response.confidence());
        assertEquals(adherence, response.adherence());
    }

    @Test
    void assembleDashboard_noWeightNoGoal_returnsPartialResponse() {
        when(userService.getUserEntityByEmail("test@test.com")).thenReturn(testUser);
        when(clock.instant()).thenReturn(Instant.parse("2026-01-01T00:00:00Z"));
        when(clock.getZone()).thenReturn(ZoneId.of("UTC"));

        LocalDate today = LocalDate.now(clock);

        DailyNutritionResponse nutritionResponse = new DailyNutritionResponse(0.0, 0.0, 0.0, 0.0);
        when(nutritionAnalyticsService.getDailyNutrition(testUser, today)).thenReturn(nutritionResponse);

        when(weightEntryService.getEntriesBetweenForUser(eq(testUser), any(), eq(today))).thenReturn(List.of());
        java.util.NavigableMap<LocalDate, Double> emptyTrend = new java.util.TreeMap<>();
        when(weightSmoothingService.computeWeightTrend(List.of())).thenReturn(emptyTrend);

        NutritionTargetResult targetResult = new NutritionTargetResult(
                null, null, null, null
        );
        when(nutritionTargetService.computeTargets(testUser, today, null, List.of(), emptyTrend))
                .thenReturn(targetResult);

        when(dataConfidenceService.computeDataConfidenceScore(testUser, today)).thenReturn(10);

        DashboardResponse response = dashboardAssembler.assembleDashboard("test@test.com", null);

        assertNotNull(response);
        assertNull(response.dailyCalorieTarget());
        assertNull(response.currentTdee());
        assertNull(response.latestWeight());
        assertNull(response.activeGoal());
        assertNull(response.adherence());
        assertEquals(10, response.confidence());
    }
}
