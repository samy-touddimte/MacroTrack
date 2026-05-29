package com.macrotrack.dashboard.service;

import com.macrotrack.dashboard.dto.DashboardResponse;
import com.macrotrack.nutrition.dto.MacroTargets;
import com.macrotrack.analytics.adherence.AdherenceMetrics;
import com.macrotrack.user.model.User;
import com.macrotrack.weight.model.WeightEntry;
import com.macrotrack.nutrition.service.FoodLogService;
import com.macrotrack.nutrition.dto.DailyNutritionResponse;
import com.macrotrack.analytics.tdee.TdeeEstimationService;
import com.macrotrack.analytics.adherence.DataConfidenceService;
import com.macrotrack.analytics.adherence.AdherenceService;
import com.macrotrack.analytics.projection.WeightSmoothingService;
import com.macrotrack.nutrition.service.NutritionAnalyticsService;
import com.macrotrack.user.service.UserInternalQueryPort;
import com.macrotrack.weight.service.WeightEntryService;
import com.macrotrack.shared.util.MetabolicConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class DashboardAssembler {

    private final UserInternalQueryPort userService;
    private final WeightEntryService weightEntryService;
    private final NutritionAnalyticsService nutritionAnalyticsService;
    private final DataConfidenceService dataConfidenceService;
    private final AdherenceService adherenceService;
    private final NutritionTargetService nutritionTargetService;
    private final WeightSmoothingService weightSmoothingService;
    private final Clock clock;

    public DashboardResponse assembleDashboard(String email, LocalDate clientDate) {
        User user = userService.getUserEntityByEmail(email);
        
        LocalDate today = clientDate != null ? clientDate : LocalDate.now(clock);

        DailyNutritionResponse todayNutrition = nutritionAnalyticsService.getDailyNutrition(user, today);

        List<WeightEntry> recentEntries = weightEntryService.getEntriesBetweenForUser(
                user, today.minusDays(MetabolicConstants.TDEE_TREND_WINDOW_DAYS), today);
                
        Double latestWeight = recentEntries.isEmpty() ? null : recentEntries.get(recentEntries.size() - 1).getWeightKg();

        Map<LocalDate, Double> trend = weightSmoothingService.computeWeightTrend(recentEntries);

        NutritionTargetResult targetResult = nutritionTargetService.computeTargets(user, today, latestWeight, recentEntries, trend);
        int confidence = dataConfidenceService.computeDataConfidenceScore(user, today);

        MacroTargets todayMacros = new MacroTargets(
                todayNutrition.proteinG(), todayNutrition.fatG(), todayNutrition.carbsG()
        );

        AdherenceMetrics adherence = null;
        if (targetResult.activeGoal() != null && (targetResult.dailyCalorieTarget() != null || targetResult.currentTdee() != null)) {
            double effectiveTarget = targetResult.dailyCalorieTarget() != null ? targetResult.dailyCalorieTarget() : targetResult.currentTdee();
            adherence = adherenceService.computeAdherence(user.getId(), targetResult.activeGoal(), effectiveTarget, today);
        }

        return new DashboardResponse(
                targetResult.dailyCalorieTarget(), 
                targetResult.currentTdee(), 
                todayNutrition.caloriesKcal(),
                latestWeight, 
                targetResult.activeGoal(), 
                targetResult.macroTargets(), 
                todayMacros, 
                confidence, 
                adherence
        );
    }


    
}
