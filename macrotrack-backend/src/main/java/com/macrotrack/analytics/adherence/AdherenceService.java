package com.macrotrack.analytics.adherence;

import com.macrotrack.analytics.adherence.AdherenceMetrics;
import com.macrotrack.goal.dto.GoalResponse;
import com.macrotrack.nutrition.service.NutritionAnalyticsService;
import com.macrotrack.shared.util.NumberUtils;
import com.macrotrack.shared.util.MetabolicConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdherenceService {

    private static final double WEIGHT_LAST_7D = 0.40;
    private static final double WEIGHT_PREV_7D = 0.35;
    private static final double WEIGHT_OLDER = 0.25;

    private final NutritionAnalyticsService nutritionAnalyticsService;

    public double computeAverageCalories(Long userId, GoalResponse activeGoal, LocalDate today) {
        LocalDate windowStart = activeGoal.startDate() != null && activeGoal.startDate().isAfter(today.minusDays(30))
                ? activeGoal.startDate()
                : today.minusDays(30);

        Map<LocalDate, Double> dailyCals = nutritionAnalyticsService.aggregateDailyCalories(userId, windowStart, today);
        if (dailyCals.isEmpty()) return 0;
        
        double total = 0;
        for (Double cals : dailyCals.values()) {
            total += cals;
        }
        return total / dailyCals.size();
    }

    public AdherenceMetrics computeAdherence(Long userId, GoalResponse activeGoal, double effectiveTarget, LocalDate today) {
        LocalDate windowStart = activeGoal.startDate() != null && activeGoal.startDate().isAfter(today.minusDays(30))
                ? activeGoal.startDate()
                : today.minusDays(30);

        long daysSinceStart = activeGoal.startDate() != null
                ? ChronoUnit.DAYS.between(activeGoal.startDate(), today) + 1
                : 0;

        if (daysSinceStart < 5) {
            return AdherenceMetrics.insufficient();
        }

        Map<LocalDate, Double> dailyCals = nutritionAnalyticsService.aggregateDailyCalories(userId, windowStart, today);

        AdherenceStats stats = calculateStats(dailyCals, effectiveTarget, windowStart, today);

        double adherenceScore = computeWeightedScore(stats);

        long expectedDays30d = Math.min(30, Math.max(1, daysSinceStart));
        double loggedAdherence = Math.min(1.0, (double) stats.daysWithLog30d() / expectedDays30d);

        return new AdherenceMetrics(
                adherenceScore,
                stats.adherence7d(),
                stats.adherence14d(),
                stats.adherence30d(),
                false,
                NumberUtils.roundToTwo(loggedAdherence)
        );
    }

    private AdherenceStats calculateStats(Map<LocalDate, Double> dailyCals, double effectiveTarget, LocalDate windowStart, LocalDate today) {
        int daysWithLog7d = 0, adherentDays7d = 0;
        int daysWithLog14d = 0, adherentDays14d = 0;
        int daysWithLog30d = 0, adherentDays30d = 0;

        LocalDate limit7d = today.minusDays(6);
        LocalDate start7d = limit7d.isBefore(windowStart) ? windowStart : limit7d;

        LocalDate limit14d = today.minusDays(13);
        LocalDate start14d = limit14d.isBefore(windowStart) ? windowStart : limit14d;

        LocalDate start30d = windowStart;

        double tolerance = Math.max(MetabolicConstants.ADHERENCE_TOLERANCE_MIN, Math.min(effectiveTarget * 0.10, MetabolicConstants.ADHERENCE_TOLERANCE_MAX));

        for (Map.Entry<LocalDate, Double> entry : dailyCals.entrySet()) {
            LocalDate logDate = entry.getKey();
            double cals = entry.getValue();

            boolean isAdherent = Math.abs(cals - effectiveTarget) <= tolerance;

            if (!logDate.isBefore(start7d)) {
                daysWithLog7d++;
                if (isAdherent) adherentDays7d++;
            }
            if (!logDate.isBefore(start14d)) {
                daysWithLog14d++;
                if (isAdherent) adherentDays14d++;
            }
            if (!logDate.isBefore(start30d)) {
                daysWithLog30d++;
                if (isAdherent) adherentDays30d++;
            }
        }

        return new AdherenceStats(
                daysWithLog7d, adherentDays7d,
                daysWithLog14d, adherentDays14d,
                daysWithLog30d, adherentDays30d
        );
    }

    private double computeWeightedScore(AdherenceStats stats) {
        int daysWithLog8_14 = stats.daysWithLog14d() - stats.daysWithLog7d();
        int adherentDays8_14 = stats.adherentDays14d() - stats.adherentDays7d();
        double adherence8_14 = daysWithLog8_14 > 0 ? (double) adherentDays8_14 / daysWithLog8_14 : 0.0;

        int daysWithLog15_30 = stats.daysWithLog30d() - stats.daysWithLog14d();
        int adherentDays15_30 = stats.adherentDays30d() - stats.adherentDays14d();
        double adherence15_30 = daysWithLog15_30 > 0 ? (double) adherentDays15_30 / daysWithLog15_30 : 0.0;

        double score = (stats.adherence7d() * WEIGHT_LAST_7D) 
                     + (adherence8_14 * WEIGHT_PREV_7D) 
                     + (adherence15_30 * WEIGHT_OLDER);
                     
        return NumberUtils.roundToTwo(score);
    }

    private record AdherenceStats(
            int daysWithLog7d, int adherentDays7d,
            int daysWithLog14d, int adherentDays14d,
            int daysWithLog30d, int adherentDays30d) {

        double adherence7d() {
            return daysWithLog7d > 0 ? (double) adherentDays7d / daysWithLog7d : 0.0;
        }

        double adherence14d() {
            return daysWithLog14d > 0 ? (double) adherentDays14d / daysWithLog14d : 0.0;
        }

        double adherence30d() {
            return daysWithLog30d > 0 ? (double) adherentDays30d / daysWithLog30d : 0.0;
        }
    }
}