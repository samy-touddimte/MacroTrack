package com.macrotrack.dashboard.service;

import com.macrotrack.analytics.projection.ProjectionResponse;
import com.macrotrack.analytics.projection.MultiScenarioProjectionResponse;
import com.macrotrack.goal.dto.GoalResponse;
import com.macrotrack.user.model.User;
import com.macrotrack.weight.model.WeightEntry;
import com.macrotrack.analytics.projection.SimulationContext;
import com.macrotrack.analytics.projection.WeightForecastResult;
import com.macrotrack.goal.model.MetabolicGoalType;
import com.macrotrack.analytics.common.DateValuePoint;
import java.util.Collections;
import java.time.temporal.ChronoUnit;

import com.macrotrack.analytics.tdee.TdeeAlgorithmService;
import com.macrotrack.analytics.projection.WeightSmoothingService;
import com.macrotrack.analytics.tdee.TdeeInternalQueryPort;
import com.macrotrack.weight.service.WeightEntryService;
import com.macrotrack.goal.service.GoalInternalQueryPort;
import com.macrotrack.user.service.UserInternalQueryPort;
import com.macrotrack.shared.util.MetabolicConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.macrotrack.analytics.projection.WeightProjectionEngine;
import com.macrotrack.analytics.bmr.BmrCalculatorService;
import com.macrotrack.analytics.adherence.AdherenceService;

import java.time.Clock;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WeightProjectionServiceImpl implements WeightProjectionService {

    private final UserInternalQueryPort userService;

    private final GoalInternalQueryPort goalService;
    private final TdeeAlgorithmService tdeeAlgorithmService;
    private final WeightSmoothingService weightSmoothingService;
    private final TdeeInternalQueryPort tdeeEstimationService;
    private final WeightEntryService weightEntryService;
    private final WeightProjectionEngine weightProjectionEngine;
    private final BmrCalculatorService bmrCalculatorService;
    private final AdherenceService adherenceService;
    private final Clock clock; 

    private record ProjectionContext(
            User user,
            GoalResponse activeGoal,
            Double currentTrendWeight,
            Double currentTdee,
            double trueInitialWeight,
            int daysElapsedAlready,
            Double latestBodyFat
    ) {}

    private ProjectionContext buildProjectionContext(String email) {
        User user = userService.getUserEntityByEmail(email);
        LocalDate today = LocalDate.now(clock);

        List<WeightEntry> entries = weightEntryService.getEntriesBetween(
                email, today.minusDays(MetabolicConstants.TDEE_TREND_WINDOW_DAYS), today
        );

        Map<LocalDate, Double> trend = weightSmoothingService.computeWeightTrend(entries);
        
        GoalResponse activeGoal = goalService.findActiveGoal(user).orElse(null);
        if (activeGoal == null) {
            // Note: MetabolicGoalType.fromWeeklyRate(0) yields MAINTAIN, returning early.
            return new ProjectionContext(user, null, null, null, Double.NaN, 0, null);
        }

        Double currentTrendWeight;
        if (trend.isEmpty()) {
            currentTrendWeight = entries.isEmpty() ? null : entries.get(entries.size() - 1).getWeightKg();
        } else {
            List<Double> trendVals = new ArrayList<>(trend.values());
            currentTrendWeight = trendVals.get(trendVals.size() - 1);
        }

        Double currentTdee = tdeeEstimationService.effectiveTdee(user, today);

        Double fallback = currentTrendWeight != null ? currentTrendWeight : weightEntryService.getFirstWeightKg(user);
        Double resolvedWeight = weightEntryService.resolveInitialWeight(user, activeGoal, fallback);

        if (resolvedWeight == null) {
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST, "No weight data available to generate projection");
        }
        double trueInitialWeight = resolvedWeight;
        int daysElapsedAlready = 0;

        if (activeGoal.startDate() != null && !today.isBefore(activeGoal.startDate())) {
            daysElapsedAlready = (int) ChronoUnit.DAYS.between(activeGoal.startDate(), today);
        }

        Double latestBodyFat = weightEntryService.getLatestBodyFatPercentage(user);

        return new ProjectionContext(user, activeGoal, currentTrendWeight, currentTdee, trueInitialWeight, daysElapsedAlready, latestBodyFat);
    }

    public ProjectionResponse getProjection(String email) {
        ProjectionContext ctx = buildProjectionContext(email);
        if (ctx.activeGoal() == null) {
            return new ProjectionResponse(List.of(), null, false);
        }

        var forecast = computeWeightProjection(
                ctx.user(), ctx.currentTrendWeight(), ctx.currentTdee(), ctx.activeGoal().weeklyRateKg(), ctx.activeGoal().targetWeightKg(),
                ctx.trueInitialWeight(), ctx.daysElapsedAlready(), ctx.latestBodyFat()
        );

        return new ProjectionResponse(forecast.points(), forecast.estimatedReachDate(), forecast.extremeDeficit());
    }

    public MultiScenarioProjectionResponse getMultiScenarioProjection(String email) {
        ProjectionContext ctx = buildProjectionContext(email);
        if (ctx.activeGoal() == null) {
            return new MultiScenarioProjectionResponse(List.of(), List.of(), null, null, false, false);
        }

        LocalDate today = LocalDate.now(clock);
        
        long daysElapsed = ctx.activeGoal().startDate() != null 
                ? ChronoUnit.DAYS.between(ctx.activeGoal().startDate(), today)
                : 0;

        boolean hasEnoughDataForEmpirical = daysElapsed >= 5;

        // Ideal projection
        double idealRate = ctx.activeGoal().weeklyRateKg();
        var idealForecast = computeWeightProjection(
                ctx.user(), ctx.currentTrendWeight(), ctx.currentTdee(), idealRate, ctx.activeGoal().targetWeightKg(),
                ctx.trueInitialWeight(), ctx.daysElapsedAlready(), ctx.latestBodyFat()
        );

        List<DateValuePoint<Double>> empiricalPoints = List.of();
        LocalDate empiricalTargetDate = null;
        boolean hasExtremeDeficit = idealForecast.extremeDeficit();

        if (hasEnoughDataForEmpirical) {
            // Get Adherence
            double expectedDailyCalorieTarget = bmrCalculatorService.computeInitialTdee(ctx.user(), ctx.currentTrendWeight(), ctx.latestBodyFat())
                                                + (idealRate * 7700.0 / 7.0); // Rough estimate for adherence target fallback if needed, but AdherenceService uses Goal
            
            var adherenceMetrics = adherenceService.computeAdherence(ctx.user().getId(), ctx.activeGoal(), expectedDailyCalorieTarget, today);
            
            // Retrieve actual average calories consumed to calculate the true empirical rate
            double avgCals = adherenceService.computeAverageCalories(ctx.user().getId(), ctx.activeGoal(), today);
            double empiricalRate = idealRate;
            if (avgCals > 0) {
                double currentDeficit = ctx.currentTdee() - avgCals;
                empiricalRate = -(currentDeficit * 7.0) / 7700.0;
            }
            
            var empiricalForecast = computeWeightProjection(
                    ctx.user(), ctx.currentTrendWeight(), ctx.currentTdee(), empiricalRate, ctx.activeGoal().targetWeightKg(),
                    ctx.trueInitialWeight(), ctx.daysElapsedAlready(), ctx.latestBodyFat()
            );
            
            empiricalPoints = empiricalForecast.points();
            empiricalTargetDate = empiricalForecast.estimatedReachDate();
            if (empiricalForecast.extremeDeficit()) {
                hasExtremeDeficit = true;
            }
        }

        return new MultiScenarioProjectionResponse(
                idealForecast.points(), 
                empiricalPoints, 
                idealForecast.estimatedReachDate(), 
                empiricalTargetDate, 
                hasEnoughDataForEmpirical,
                hasExtremeDeficit
        );
    }
    
    private WeightForecastResult computeWeightProjection(
            User user,
            Double currentTrendWeight,
            Double currentTdee,
            Double weeklyRate,
            Double targetWeightKg,
            double trueInitialWeight,
            int daysElapsedAlready,
            Double latestBodyFat
    ) {
        if (user == null || currentTrendWeight == null || currentTdee == null
                || weeklyRate == null || targetWeightKg == null) {
            return new WeightForecastResult(Collections.emptyList(), null, MetabolicGoalType.MAINTAIN, false);
        }

        Double mifflinNow = bmrCalculatorService.computeInitialTdee(user, currentTrendWeight, latestBodyFat);
        double adaptation = currentTdee - (mifflinNow != null ? mifflinNow : currentTdee);
        
        var forecast = weightProjectionEngine.generateWeightProjection(
                new SimulationContext(
                        currentTrendWeight,
                        targetWeightKg,
                        weeklyRate,
                        LocalDate.now(clock),
                        currentTdee,
                        user,
                        adaptation,
                        trueInitialWeight,
                        daysElapsedAlready,
                        latestBodyFat
                )
        );
        
        return new WeightForecastResult(forecast.points(), forecast.estimatedReachDate(), forecast.goalType(), forecast.extremeDeficit());
    }
}
