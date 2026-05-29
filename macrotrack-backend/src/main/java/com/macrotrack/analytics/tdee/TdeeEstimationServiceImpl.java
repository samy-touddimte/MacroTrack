package com.macrotrack.analytics.tdee;

import com.macrotrack.analytics.common.DateValuePoint;
import com.macrotrack.analytics.tdee.TdeeResponse;
import com.macrotrack.user.model.User;
import com.macrotrack.user.service.UserInternalQueryPort;
import com.macrotrack.analytics.projection.MetabolicAdaptationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import com.macrotrack.weight.service.WeightEntryService;

/**
 * Implementation of TDEE estimation logic.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class TdeeEstimationServiceImpl implements TdeeEstimationService, TdeeInternalQueryPort {

    private final UserInternalQueryPort userService;
    private final TdeeHistoryProvider tdeeHistoryProvider;
    private final TdeeCalculationEngine tdeeCalculationEngine;
    private final TdeeAlgorithmService tdeeAlgorithmService;
    private final MetabolicAdaptationService metabolicAdaptationService;
    private final WeightEntryService weightEntryService;

    public TdeeResponse getTdee(String email, LocalDate from, LocalDate to) {
        User user = userService.getUserEntityByEmail(email);
        TdeeHistoryContext context = tdeeHistoryProvider.getHistoryContextForRange(user, from, to);

        // Dual calculation paths documented:
        // 1. estimatedTdee: Computed via TdeeAlgorithmService. Provides a raw, unadapted historical curve.
        // 2. currentTdee: Computed via TdeeCalculationEngine. Provides the single, current effective TDEE 
        //    (blended with static BMR and fully adapted).
        Double latestBodyFat = weightEntryService.getLatestBodyFatPercentage(user);
        Map<LocalDate, Double> estimatedTdee = tdeeAlgorithmService.computeEstimatedTdee(user, context.trend(), context.dailyCalories(), latestBodyFat);
        Double currentTdee = effectiveTdee(user, to);

        Double currentWeight = weightEntryService.resolveCurrentWeight(user, context.trend());
        Double initialWeight = weightEntryService.resolveInitialWeight(user, context.activeGoal(), currentWeight);

        boolean isEmpirical = true;
        if (estimatedTdee.isEmpty() && currentTdee != null && !context.trend().isEmpty()) {
            estimatedTdee = tdeeAlgorithmService.buildInitialTdeeLine(context.trend().keySet(), currentTdee);
            isEmpirical = false;
        }


        List<DateValuePoint<Double>> tdeePoints = estimatedTdee.entrySet().stream()
                .map(entry -> {
                    LocalDate date = entry.getKey();
                    Double rawTdee = entry.getValue();
                    Double historicalWeight = context.trend().get(date);
                    
                    if (historicalWeight != null) {
                        Double baseWeight = initialWeight != null ? initialWeight : historicalWeight;
                        Double weeklyRate = context.activeGoal() != null ? context.activeGoal().weeklyRateKg() : null;
                        
                        int dayInPlan = 0;
                        if (context.activeGoal() != null && context.activeGoal().startDate() != null) {
                            dayInPlan = (int) ChronoUnit.DAYS.between(context.activeGoal().startDate(), date);
                            if (dayInPlan < 0) dayInPlan = 0;
                        }
                        
                        Double adjustedTdee = metabolicAdaptationService.adjustTdee(
                                rawTdee, baseWeight, historicalWeight, weeklyRate, dayInPlan, false);
                        return new DateValuePoint<>(date, adjustedTdee);
                    }
                    return new DateValuePoint<>(date, rawTdee);
                })
                .toList();

        List<DateValuePoint<Double>> calPoints = context.dailyCalories().entrySet().stream()
                .map(e -> new DateValuePoint<>(e.getKey(), e.getValue()))
                .toList();
                
        return new TdeeResponse(tdeePoints, calPoints, currentTdee, isEmpirical);
    }

    public Double effectiveTdee(User user, LocalDate targetDate) {
        TdeeHistoryContext context = tdeeHistoryProvider.getHistoryContext(user, targetDate);
        return tdeeCalculationEngine.computeEffectiveTdee(context);
    }
}
