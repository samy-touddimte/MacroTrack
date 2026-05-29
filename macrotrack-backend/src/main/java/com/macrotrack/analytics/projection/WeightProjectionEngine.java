package com.macrotrack.analytics.projection;

import com.macrotrack.analytics.common.DateValuePoint;
import com.macrotrack.analytics.projection.WeightForecastResult;
import com.macrotrack.user.model.BiologicalSex;
import com.macrotrack.goal.model.MetabolicGoalType;

import com.macrotrack.user.model.User;
import com.macrotrack.shared.util.NumberUtils;
import com.macrotrack.shared.util.MetabolicConstants;
import com.macrotrack.shared.util.NutritionUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import com.macrotrack.analytics.bmr.BmrCalculatorService;

@Service
@RequiredArgsConstructor
public class WeightProjectionEngine {

    private static final double CONVERGENCE_THRESHOLD = 0.0005;
    private static final int EARLY_EXIT_CHECK_DAYS = 730;

    private final MetabolicAdaptationService metabolicAdaptationService;
    private final BmrCalculatorService bmrCalculatorService;

    public WeightForecastResult generateWeightProjection(SimulationContext ctx) {
        MetabolicGoalType goalType = MetabolicGoalType.fromWeeklyRate(ctx.weeklyRate());
        List<DateValuePoint<Double>> points = new ArrayList<>();
        points.add(new DateValuePoint<>(ctx.startDate(), NumberUtils.roundToTwo(ctx.currentWeight())));

        // Le short-circuit pour MAINTAIN empêchait la projection empirique (taux proche de 0) de s'afficher.
        // La simulation naturelle va désormais tracer une courbe plate.

        double currentWeightAtSimulationStart = ctx.currentWeight();
        double simWeight = ctx.currentWeight();
        LocalDate estimatedReach = null;
        boolean losing = ctx.goalWeight() < ctx.currentWeight();
        boolean extremeDeficit = false;

        double energyDensity = NutritionUtils.resolveEnergyDensity(ctx.weeklyRate(), ctx.user().getTrainingType(), ctx.user().getTrainingExperience(), ctx.user().getSex());
        double minClinical = (ctx.user() != null && ctx.user().getSex() == BiologicalSex.MALE)
                ? MetabolicConstants.MIN_CLINICAL_CALORIES_MALE
                : MetabolicConstants.MIN_CLINICAL_CALORIES_FEMALE;

        for (int day = 1; day <= MetabolicConstants.MAX_PROJECTION_DAYS; day++) {
            int absoluteDay = ctx.daysElapsedAlready() + day;
            Double mifflin = bmrCalculatorService.computeInitialTdee(ctx.user(), simWeight, ctx.latestBodyFat());
            double simTdee = (mifflin != null ? mifflin : ctx.baseTdee()) + ctx.measuredAdaptation();

            simTdee = metabolicAdaptationService.applyMetabolicAdaptations(
                    simTdee, goalType, ctx.trueInitialWeight(), currentWeightAtSimulationStart, simWeight, ctx.goalWeight(), absoluteDay, ctx.weeklyRate(), true);

            double theoreticalTarget = simTdee + (ctx.weeklyRate() * energyDensity / 7.0);

            double scientificFloor = Math.max(minClinical, simTdee * 0.65);
            double actualCalorieTarget = Math.max(scientificFloor, theoreticalTarget);

            if (ctx.weeklyRate() < 0 && theoreticalTarget < minClinical) {
                extremeDeficit = true;
            }

            double fatDelta = (theoreticalTarget - simTdee) / energyDensity;

            double phaseBonus = computePhaseWeightBonus(goalType, absoluteDay);
            simWeight += fatDelta + phaseBonus;
            LocalDate pointDate = ctx.startDate().plusDays(day);
            points.add(new DateValuePoint<>(pointDate, NumberUtils.roundToTwo(simWeight)));

            if (hasReachedGoal(simWeight, ctx.goalWeight(), losing)) {
                estimatedReach = pointDate;
                break;
            }
            if (day > EARLY_EXIT_CHECK_DAYS && Math.abs(fatDelta + phaseBonus) < CONVERGENCE_THRESHOLD) {
                break;
            }
        }

        return new WeightForecastResult(sampleWeekly(points), estimatedReach, goalType, extremeDeficit);
    }

    private boolean hasReachedGoal(double simWeight, double goalWeight, boolean losing) {
        return losing ? simWeight <= goalWeight : simWeight >= goalWeight;
    }

    private double computePhaseWeightBonus(MetabolicGoalType goalType, int dayInPlan) {
        double alpha = MetabolicConstants.PHASE_BONUS_ALPHA;
        if (goalType == MetabolicGoalType.LOSS) {
            if (dayInPlan <= 14) {
                // The geometric series sum approaches WATER_WEIGHT_LOSS_MULTIPLIER as n -> infinity
                double dailyDrop = MetabolicConstants.WATER_WEIGHT_LOSS_MULTIPLIER * alpha * Math.pow(1.0 - alpha, dayInPlan - 1);
                return -dailyDrop;
            }
        } else if (goalType == MetabolicGoalType.GAIN) {
            if (dayInPlan <= 14) {
                // The geometric series sum approaches WATER_WEIGHT_GAIN_MULTIPLIER as n -> infinity
                double dailyGain = MetabolicConstants.WATER_WEIGHT_GAIN_MULTIPLIER * alpha * Math.pow(1.0 - alpha, dayInPlan - 1);
                return dailyGain;
            }
        }
        return 0.0;
    }
    public List<DateValuePoint<Double>> sampleWeekly(List<DateValuePoint<Double>> daily) {
        if (daily == null || daily.isEmpty()) {
            return List.of();
        }
        List<DateValuePoint<Double>> weekly = new ArrayList<>();
        for (int i = 0; i < daily.size(); i += 7) {
            weekly.add(daily.get(i));
        }
        DateValuePoint<Double> last = daily.get(daily.size() - 1);
        if (weekly.isEmpty() || !weekly.get(weekly.size() - 1).date().equals(last.date())) {
            weekly.add(last);
        }
        return weekly;
    }
}
