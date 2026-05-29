package com.macrotrack.analytics.tdee;

import com.macrotrack.goal.dto.GoalResponse;
import com.macrotrack.user.model.User;
import com.macrotrack.weight.model.WeightEntry;
import com.macrotrack.weight.repository.WeightEntryRepository;
import com.macrotrack.goal.service.GoalInternalQueryPort;
import com.macrotrack.nutrition.service.NutritionAnalyticsService;
import com.macrotrack.analytics.projection.WeightSmoothingService;
import com.macrotrack.shared.util.MetabolicConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TdeeHistoryProvider {

    private final WeightEntryRepository weightEntryRepository;
    private final GoalInternalQueryPort goalService;
    private final WeightSmoothingService weightSmoothingService;
    private final NutritionAnalyticsService nutritionAnalyticsService;

    public TdeeHistoryContext getHistoryContext(User user, LocalDate targetDate) {
        GoalResponse activeGoal = goalService.findActiveGoal(user).orElse(null);
        LocalDate to = targetDate;
        
        LocalDate from = to.minusDays(MetabolicConstants.TDEE_TREND_WINDOW_DAYS);
        if (from.isAfter(to)) from = to;

        return getHistoryContextForRange(user, from, to, activeGoal);
    }

    public TdeeHistoryContext getHistoryContextForRange(User user, LocalDate from, LocalDate to) {
        GoalResponse activeGoal = goalService.findActiveGoal(user).orElse(null);
        return getHistoryContextForRange(user, from, to, activeGoal);
    }

    public TdeeHistoryContext getHistoryContextForRange(User user, LocalDate from, LocalDate to, GoalResponse activeGoal) {
        List<WeightEntry> entries = weightEntryRepository.findByUserIdAndDateBetweenOrderByDateAsc(user.getId(), from, to);
        Map<LocalDate, Double> trend = weightSmoothingService.computeWeightTrend(entries);
        Map<LocalDate, Double> dailyCalories = nutritionAnalyticsService.aggregateDailyCalories(user, from, to);

        return new TdeeHistoryContext(user, from, to, activeGoal, entries, trend, dailyCalories);
    }
}
