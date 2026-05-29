package com.macrotrack.nutrition.dto;

import java.time.LocalDate;

public record DailyCalorieAggregation(LocalDate date, Double caloriesKcal) {
}
