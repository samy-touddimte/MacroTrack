package com.macrotrack.analytics.projection;
import com.macrotrack.analytics.common.DateValuePoint;

import java.util.List;

public record MetabolicForecastResponse(
    List<DateValuePoint<Double>> forecastPoints,
    java.time.LocalDate estimatedReachDate,
    boolean extremeDeficit
) {}