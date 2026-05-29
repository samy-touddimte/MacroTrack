package com.macrotrack.analytics.projection;

import com.macrotrack.goal.model.MetabolicGoalType;
import com.macrotrack.shared.util.MetabolicConstants;
import org.springframework.stereotype.Component;

@Component
public class LossMetabolicAdaptationStrategy implements MetabolicAdaptationStrategy {

    @Override
    public MetabolicGoalType getSupportedGoalType() {
        return MetabolicGoalType.LOSS;
    }

    @Override
    public double applyAdaptation(AdaptationContext context) {
        double result = context.baseTdee();

        if (context.trueInitialWeight() > 0) {
            double totalKgLost = Math.max(0, context.trueInitialWeight() - context.simWeight());
            double lossFraction = totalKgLost / context.trueInitialWeight();

            if (lossFraction > MetabolicConstants.ADAPT_THERMO_START) {
                double adaptivePenalty = lossFraction >= MetabolicConstants.ADAPT_THERMO_FULL
                        ? MetabolicConstants.ADAPT_MAX_PENALTY
                        : MetabolicConstants.ADAPT_MAX_PENALTY
                        * (lossFraction - MetabolicConstants.ADAPT_THERMO_START)
                        / (MetabolicConstants.ADAPT_THERMO_FULL - MetabolicConstants.ADAPT_THERMO_START);

                double durationFactor = Math.min(1.0, context.absoluteDayInPlan() / 90.0);
                adaptivePenalty *= (0.5 + 0.5 * durationFactor);

                result *= (1.0 - adaptivePenalty);
            }
        }

        return Math.max(MetabolicConstants.MIN_SURVIVAL_CALORIES, result);
    }
}
