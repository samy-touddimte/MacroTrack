package com.macrotrack.analytics.tdee;

import com.macrotrack.goal.dto.GoalResponse;
import com.macrotrack.user.model.User;
import com.macrotrack.weight.model.WeightEntry;

import java.time.LocalDate;
import java.util.List;
import java.util.Collections;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public record TdeeHistoryContext(
    User user,
    LocalDate from,
    LocalDate to,
    GoalResponse activeGoal,
    List<WeightEntry> entries,
    Map<LocalDate, Double> trend,
    Map<LocalDate, Double> dailyCalories
) {
    public TdeeHistoryContext {
        entries = entries != null ? Collections.unmodifiableList(new ArrayList<>(entries)) : Collections.emptyList();
        trend = trend != null ? Collections.unmodifiableMap(new LinkedHashMap<>(trend)) : Collections.emptyMap();
        dailyCalories = dailyCalories != null ? Collections.unmodifiableMap(new LinkedHashMap<>(dailyCalories)) : Collections.emptyMap();
    }
}
