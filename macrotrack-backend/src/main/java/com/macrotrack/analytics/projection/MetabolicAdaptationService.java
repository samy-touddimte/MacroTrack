package com.macrotrack.analytics.projection;

import com.macrotrack.goal.model.MetabolicGoalType;
import com.macrotrack.shared.util.MetabolicConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class MetabolicAdaptationService {

    private final Map<MetabolicGoalType, MetabolicAdaptationStrategy> strategies;

    public MetabolicAdaptationService(List<MetabolicAdaptationStrategy> strategyList) {
        this.strategies = strategyList.stream()
                .collect(Collectors.toMap(MetabolicAdaptationStrategy::getSupportedGoalType, Function.identity()));
    }

    public double adjustTdee(
            double rawEstimatedTdee,
            Double initialWeight,
            double currentWeight,
            Double weeklyRate,
            int absoluteDayInPlan,
            boolean isFullyAdapted
    ) {
        if (rawEstimatedTdee <= 0 || initialWeight == null || initialWeight <= 0 || weeklyRate == null || Math.abs(weeklyRate) < 1e-9) {
            if (initialWeight == null) {
                log.warn("Cannot apply metabolic adaptation: initialWeight is null. Returning raw TDEE.");
            }
            return rawEstimatedTdee;
        }

        MetabolicGoalType goalType = MetabolicGoalType.fromWeeklyRate(weeklyRate);
        return applyMetabolicAdaptations(
                rawEstimatedTdee,
                goalType,
                initialWeight,
                currentWeight,
                currentWeight,
                0.0,
                absoluteDayInPlan,
                weeklyRate,
                isFullyAdapted);
    }

    public double applyMetabolicAdaptations(
            double baseTdee,
            MetabolicGoalType goalType,
            double trueInitialWeight,
            double currentWeightAtSimulationStart,
            double simWeight,
            double goalWeight,
            int absoluteDayInPlan,
            double weeklyRate,
            boolean isFullyAdapted
    ) {
        MetabolicAdaptationStrategy strategy = strategies.getOrDefault(goalType, strategies.get(MetabolicGoalType.MAINTAIN));
        AdaptationContext context = new AdaptationContext(
                baseTdee, trueInitialWeight, currentWeightAtSimulationStart,
                simWeight, goalWeight, absoluteDayInPlan, weeklyRate, isFullyAdapted
        );
        return strategy.applyAdaptation(context);
    }
}
