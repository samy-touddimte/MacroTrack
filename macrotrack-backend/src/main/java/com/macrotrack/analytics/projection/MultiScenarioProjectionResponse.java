package com.macrotrack.analytics.projection;
import com.macrotrack.analytics.common.DateValuePoint;

import java.util.List;

public record MultiScenarioProjectionResponse(
    List<DateValuePoint<Double>> idealPoints,
    List<DateValuePoint<Double>> empiricalPoints,
    java.time.LocalDate idealDate,
    java.time.LocalDate empiricalDate,
    boolean hasEnoughDataForEmpirical,
    boolean extremeDeficit
) {}