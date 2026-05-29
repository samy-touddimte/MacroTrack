package com.macrotrack.analytics.projection;

import com.macrotrack.goal.model.MetabolicGoalType;
import com.macrotrack.shared.util.MetabolicConstants;
import org.springframework.stereotype.Component;

@Component
public class MaintainMetabolicAdaptationStrategy implements MetabolicAdaptationStrategy {

    @Override
    public MetabolicGoalType getSupportedGoalType() {
        return MetabolicGoalType.MAINTAIN;
    }

    @Override
    public double applyAdaptation(AdaptationContext context) {
        return Math.max(MetabolicConstants.MIN_SURVIVAL_CALORIES, context.baseTdee());
    }
}
