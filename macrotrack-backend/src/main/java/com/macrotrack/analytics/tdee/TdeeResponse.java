package com.macrotrack.analytics.tdee;
import com.macrotrack.analytics.common.DateValuePoint;
import java.util.List;
public record TdeeResponse(
    List<DateValuePoint<Double>> tdeeEstimated, 
    List<DateValuePoint<Double>> caloriesConsumedKcal, 
    Double currentTdee,
    boolean isEmpirical
) {}