package com.macrotrack.goal.service;

import com.macrotrack.shared.exception.GoalValidationException;

public class GoalValidator {
    
    private GoalValidator() {}

    public static void validateGoal(Double currentWeight, Double targetWeight, Double weeklyRateKg) {
        if (currentWeight != null && targetWeight != null && weeklyRateKg != null) {
            if (weeklyRateKg < 0 && targetWeight >= currentWeight) {
                throw new GoalValidationException("Target weight must be below current weight for a loss goal");
            }
            if (weeklyRateKg > 0 && targetWeight <= currentWeight) {
                throw new GoalValidationException("Target weight must be above current weight for a gain goal");
            }
            if (Math.abs(weeklyRateKg) < 0.01 && !targetWeight.equals(currentWeight)) {
                throw new GoalValidationException("MAINTAIN goal requires target weight equal to current weight");
            }
            if (weeklyRateKg != 0 && Math.abs(currentWeight - targetWeight) < Math.abs(weeklyRateKg)) {
                throw new GoalValidationException("Weekly rate magnitude cannot exceed the total weight difference to target");
            }
        }
    }
}
