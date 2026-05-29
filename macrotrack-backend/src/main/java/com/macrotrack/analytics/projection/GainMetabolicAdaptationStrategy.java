package com.macrotrack.analytics.projection;

import com.macrotrack.goal.model.MetabolicGoalType;
import com.macrotrack.shared.util.MetabolicConstants;
import org.springframework.stereotype.Component;

@Component
public class GainMetabolicAdaptationStrategy implements MetabolicAdaptationStrategy {

    @Override
    public MetabolicGoalType getSupportedGoalType() {
        return MetabolicGoalType.GAIN;
    }

    @Override
    public double applyAdaptation(AdaptationContext context) {
        double result = context.baseTdee();

        if (context.weeklyRate() > 0 && context.absoluteDayInPlan() >= MetabolicConstants.NEAT_ONSET_DAYS) {
            double ramp = Math.min(1.0, (double) (context.absoluteDayInPlan() - MetabolicConstants.NEAT_ONSET_DAYS) / MetabolicConstants.NEAT_RAMP_DAYS);
            result *= (1.0 + MetabolicConstants.NEAT_MAX_BOOST * ramp);
        }

        return Math.max(MetabolicConstants.MIN_SURVIVAL_CALORIES, result);
    }
}
