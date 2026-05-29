package com.macrotrack.analytics.tdee;

import com.macrotrack.goal.dto.GoalResponse;
import com.macrotrack.user.model.User;
import com.macrotrack.analytics.bmr.BmrCalculatorService;
import com.macrotrack.analytics.projection.MetabolicAdaptationService;
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
import com.macrotrack.shared.util.NumberUtils;

@Service
@RequiredArgsConstructor
@Slf4j
public class TdeeCalculationEngine {

    private final TdeeAlgorithmService tdeeAlgorithmService;
    private final BmrCalculatorService bmrCalculatorService;
    private final MetabolicAdaptationService metabolicAdaptationService;
    private final WeightEntryService weightEntryService;
    private final Clock clock;

    public Double computeEffectiveTdee(TdeeHistoryContext context) {
        User user = context.user();
        Double latestBodyFat = null;
        if (!context.entries().isEmpty()) {
            latestBodyFat = context.entries().get(context.entries().size() - 1).getBodyFatPercentage();
        } else {
            latestBodyFat = weightEntryService.getLatestBodyFatPercentage(user);
        }

        if (context.entries().isEmpty()) {
            Double firstWeight = weightEntryService.getFirstWeightKg(user);
            if (firstWeight == null) return MetabolicConstants.DEFAULT_TDEE_KCAL;
            Double tdee = bmrCalculatorService.computeInitialTdee(user, firstWeight, latestBodyFat);
            return tdee != null ? tdee : MetabolicConstants.DEFAULT_TDEE_KCAL;
        }

        double lastWeight = context.entries().get(context.entries().size() - 1).getWeightKg();
        Double initialTdee = bmrCalculatorService.computeInitialTdee(user, lastWeight, latestBodyFat);
        double staticTdee = initialTdee != null ? initialTdee : MetabolicConstants.DEFAULT_TDEE_KCAL;

        long daysWithLog = context.dailyCalories().size();

        Double computedTdee = computeDynamicTdee(user, context.trend(), context.dailyCalories(), daysWithLog, latestBodyFat);
        
        Double result = blendWithConfidence(computedTdee, staticTdee, daysWithLog);

        return applyAdaptations(user, context.activeGoal(), result, lastWeight);
    }

    private Double computeDynamicTdee(User user, Map<LocalDate, Double> trend, Map<LocalDate, Double> dailyCalories, long daysWithLog, Double latestBodyFat) {
        if (daysWithLog >= 7 && trend.size() >= 8) {
            Map<LocalDate, Double> estTdee = tdeeAlgorithmService.computeEstimatedTdee(user, trend, dailyCalories, latestBodyFat);
            if (!estTdee.isEmpty()) {
                List<Double> vals = new ArrayList<>(estTdee.values());
                double avg = vals.subList(Math.max(0, vals.size() - MetabolicConstants.TDEE_AVERAGING_WINDOW_DAYS), vals.size())
                        .stream().mapToDouble(Double::doubleValue).average().orElse(0);
                if (avg > MetabolicConstants.MIN_SURVIVAL_CALORIES && avg < MetabolicConstants.MAX_TDEE_LIMIT) {
                    return avg;
                } else {
                    log.warn("Computed TDEE ({}) is out of bounds (800-6000). Applying theoretical fallback.", NumberUtils.roundToTwo(avg));
                }
            }
        }
        return null;
    }

    private Double blendWithConfidence(Double computedTdee, double staticTdee, long daysWithLog) {
        if (computedTdee == null) return staticTdee;
        double confidence = Math.min(1.0, (double) daysWithLog / MetabolicConstants.TDEE_FULL_CONFIDENCE_DAYS);
        return (computedTdee * confidence) + (staticTdee * (1.0 - confidence));
    }

    private Double applyAdaptations(User user, GoalResponse activeGoal, Double rawTdee, double lastWeight) {
        Double initialWeight = weightEntryService.resolveInitialWeight(user, activeGoal, lastWeight);
        Double weeklyRate = activeGoal != null ? activeGoal.weeklyRateKg() : null;
        
        int dayInPlan = 0;
        if (activeGoal != null && activeGoal.startDate() != null) {
            dayInPlan = (int) java.time.temporal.ChronoUnit.DAYS.between(activeGoal.startDate(), LocalDate.now(clock));
            if (dayInPlan < 0) dayInPlan = 0;
        }
        
        return metabolicAdaptationService.adjustTdee(rawTdee, initialWeight, lastWeight, weeklyRate, dayInPlan, false);
    }
}
