package com.macrotrack.nutrition.service;

import com.macrotrack.nutrition.dto.DailyNutritionResponse;
import com.macrotrack.user.model.User;

import java.time.LocalDate;
import java.util.Map;

public interface NutritionAnalyticsService {
    DailyNutritionResponse getDailyNutrition(String email, LocalDate date);
    DailyNutritionResponse getDailyNutrition(User user, LocalDate date);
    Map<LocalDate, Double> aggregateDailyCalories(User user, LocalDate from, LocalDate to);
    Map<LocalDate, Double> aggregateDailyCalories(Long userId, LocalDate from, LocalDate to);
}
