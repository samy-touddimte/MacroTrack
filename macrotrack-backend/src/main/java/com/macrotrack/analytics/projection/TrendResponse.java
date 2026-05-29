package com.macrotrack.analytics.projection;
import com.macrotrack.analytics.common.DateValuePoint;

import java.util.List;

public record TrendResponse(
    List<DateValuePoint<Double>> dynamicTrend,
    TrendVariations variations
) {}