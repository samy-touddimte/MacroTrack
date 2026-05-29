package com.macrotrack.nutrition.dto;

public record DailyNutritionResponse(
        double caloriesKcal,
        double proteinG,
        double fatG,
        double carbsG
) {}
