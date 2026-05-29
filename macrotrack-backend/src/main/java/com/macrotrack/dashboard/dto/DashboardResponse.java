package com.macrotrack.dashboard.dto;
import com.macrotrack.analytics.adherence.AdherenceMetrics;
import com.macrotrack.nutrition.dto.MacroTargets;
import org.springframework.lang.Nullable;
import io.swagger.v3.oas.annotations.media.Schema;
import com.macrotrack.goal.dto.GoalResponse;

public record DashboardResponse(
        Double dailyCalorieTarget,
        Double currentTdee,
        Double todayCaloriesKcal,
        @Nullable @Schema(nullable = true, description = "Most recent weight entry for the user. May be null if no weight has been logged.")
        Double latestWeight,
        GoalResponse activeGoal,
        MacroTargets macroTargets,
        MacroTargets todayMacros,
        @Nullable @Schema(nullable = true, description = "Data confidence score (1 to 5, where 5 is highest confidence). May be null if there is insufficient tracking data.")
        Integer confidence,
        AdherenceMetrics adherence
) {}