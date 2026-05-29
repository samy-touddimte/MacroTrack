package com.macrotrack.nutrition.service;

import com.macrotrack.nutrition.dto.DailyNutritionResponse;
import com.macrotrack.nutrition.model.FoodLog;
import com.macrotrack.user.model.User;
import com.macrotrack.nutrition.repository.FoodLogRepository;
import com.macrotrack.user.service.UserInternalQueryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NutritionAnalyticsServiceImpl implements NutritionAnalyticsService {

    private final FoodLogRepository foodLogRepository;
    private final UserInternalQueryPort userService;

    @Override
    public DailyNutritionResponse getDailyNutrition(String email, LocalDate date) {
        User user = userService.getUserEntityByEmail(email);
        return getDailyNutrition(user, date);
    }

    @Override
    public DailyNutritionResponse getDailyNutrition(User user, LocalDate date) {
        var agg = foodLogRepository.aggregateDailyMacros(user.getId(), date);
        if (agg == null || agg.caloriesKcal() == null) {
            return new DailyNutritionResponse(0.0, 0.0, 0.0, 0.0);
        }
        return new DailyNutritionResponse(
                agg.caloriesKcal() != null ? agg.caloriesKcal() : 0.0,
                agg.proteinG() != null ? agg.proteinG() : 0.0,
                agg.fatG() != null ? agg.fatG() : 0.0,
                agg.carbsG() != null ? agg.carbsG() : 0.0
        );
    }

    @Override
    public Map<LocalDate, Double> aggregateDailyCalories(User user, LocalDate from, LocalDate to) {
        Map<LocalDate, Double> dailyCalories = new LinkedHashMap<>();
        foodLogRepository.aggregateDailyCaloriesBetween(user.getId(), from, to)
                .forEach(agg -> dailyCalories.put(agg.date(), agg.caloriesKcal()));
        return dailyCalories;
    }

    @Override
    public Map<LocalDate, Double> aggregateDailyCalories(Long userId, LocalDate from, LocalDate to) {
        Map<LocalDate, Double> dailyCalories = new LinkedHashMap<>();
        foodLogRepository.aggregateDailyCaloriesBetween(userId, from, to)
                .forEach(agg -> dailyCalories.put(agg.date(), agg.caloriesKcal()));
        return dailyCalories;
    }
}
