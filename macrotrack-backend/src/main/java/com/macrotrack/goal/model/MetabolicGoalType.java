package com.macrotrack.goal.model;

public enum MetabolicGoalType {
    LOSS,
    GAIN,
    MAINTAIN;

    public static MetabolicGoalType fromWeeklyRate(double weeklyRate) {
        if (Math.abs(weeklyRate) < 0.01) {
            return MAINTAIN;
        }
        return weeklyRate < 0 ? LOSS : GAIN;
    }
}
