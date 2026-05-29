package com.macrotrack.analytics.projection;
import com.macrotrack.analytics.common.DateValuePoint;

import java.util.List;

public record ProjectionResponse(
    List<DateValuePoint<Double>> points, 
    java.time.LocalDate targetReachedDate,
    boolean extremeDeficit
) {}