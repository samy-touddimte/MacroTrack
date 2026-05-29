package com.macrotrack.analytics.projection;

public record AdaptationContext(
    double baseTdee,
    double trueInitialWeight,
    double currentWeightAtSimulationStart,
    double simWeight,
    double goalWeight,
    int absoluteDayInPlan,
    double weeklyRate,
    boolean isFullyAdapted
) {}
