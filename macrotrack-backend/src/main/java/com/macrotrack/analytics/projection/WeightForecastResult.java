package com.macrotrack.analytics.projection;

import com.macrotrack.goal.model.MetabolicGoalType;
import com.macrotrack.analytics.common.DateValuePoint;

import java.time.LocalDate;
import java.util.List;

public record WeightForecastResult(
        List<DateValuePoint<Double>> points,
        LocalDate estimatedReachDate,
        MetabolicGoalType goalType,
        boolean extremeDeficit
) {}
