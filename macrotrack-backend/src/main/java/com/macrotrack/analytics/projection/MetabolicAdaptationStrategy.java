package com.macrotrack.analytics.projection;

import com.macrotrack.goal.model.MetabolicGoalType;

public interface MetabolicAdaptationStrategy {
    MetabolicGoalType getSupportedGoalType();

    double applyAdaptation(AdaptationContext context);
}
