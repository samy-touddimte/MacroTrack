package com.macrotrack.dashboard.service;

import com.macrotrack.goal.dto.GoalResponse;
import com.macrotrack.nutrition.dto.MacroTargets;
import com.macrotrack.user.model.User;
import com.macrotrack.weight.model.WeightEntry;
import com.macrotrack.weight.service.WeightEntryService;
import com.macrotrack.analytics.tdee.TdeeInternalQueryPort;
import com.macrotrack.goal.service.GoalInternalQueryPort;
import com.macrotrack.nutrition.service.MacroCalculatorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class NutritionTargetService {

    private final GoalInternalQueryPort goalService;
    private final TdeeInternalQueryPort tdeeEstimationService;
    private final WeightEntryService weightEntryService;
    private final MacroCalculatorService macroCalculatorService;

    public NutritionTargetResult computeTargets(User user, LocalDate today, Double todayWeight, 
                                                List<WeightEntry> recentEntries, Map<LocalDate, Double> trend) {
        Double currentTdee = tdeeEstimationService.effectiveTdee(user, today);
        GoalResponse activeGoal = goalService.findActiveGoal(user).orElse(null);

        Double dailyCalorieTarget = (activeGoal != null && currentTdee != null && activeGoal.weeklyRateKg() != null) 
            ? macroCalculatorService.computeDailyCalorieTarget(user, currentTdee, activeGoal.weeklyRateKg()) 
            : null;

        MacroTargets macroTargets = null;
        if (dailyCalorieTarget != null) {
            Double effectiveWeight = weightEntryService.resolveCurrentWeight(user, trend);
            if (effectiveWeight != null) {
                double weeklyRate = activeGoal != null ? activeGoal.weeklyRateKg() : 0.0;
                macroTargets = macroCalculatorService.computeMacroTargets(effectiveWeight, user.getBodyFatPercentage(), dailyCalorieTarget, weeklyRate);
            }
        }

        return new NutritionTargetResult(currentTdee, activeGoal, dailyCalorieTarget, macroTargets);
    }
}
