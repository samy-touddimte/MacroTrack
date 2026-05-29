package com.macrotrack.nutrition.dto;

public record DailyMacroAggregation(
    Double caloriesKcal,
    Double proteinG,
    Double fatG,
    Double carbsG
) {}
