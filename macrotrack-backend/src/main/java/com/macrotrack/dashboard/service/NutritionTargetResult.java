package com.macrotrack.dashboard.service;

import com.macrotrack.goal.dto.GoalResponse;
import com.macrotrack.nutrition.dto.MacroTargets;

import org.springframework.lang.Nullable;

public record NutritionTargetResult(
    @Nullable Double currentTdee,
    @Nullable GoalResponse activeGoal,
    @Nullable Double dailyCalorieTarget,
    /**
     * Can be null if dailyCalorieTarget is null or if there is no weight data.
     * The frontend must handle this case gracefully.
     */
    @Nullable MacroTargets macroTargets
) {}
