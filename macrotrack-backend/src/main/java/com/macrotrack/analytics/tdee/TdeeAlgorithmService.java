package com.macrotrack.analytics.tdee;
import com.macrotrack.user.model.User;
import com.macrotrack.shared.util.MetabolicConstants;
import com.macrotrack.shared.util.NutritionUtils;

import com.macrotrack.shared.util.NumberUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.macrotrack.analytics.bmr.BmrCalculatorService;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Collection;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class TdeeAlgorithmService {

    private final BmrCalculatorService bmrCalculatorService;
    
    private static final int MIN_TDEE_DAYS = 5;

    public Map<LocalDate, Double> computeEstimatedTdee(
            User user,
            Map<LocalDate, Double> weightTrend,
            Map<LocalDate, Double> dailyCalories,
            Double latestBodyFat
    ) {
        Map<LocalDate, Double> estimated = new LinkedHashMap<>();
        if (weightTrend == null || weightTrend.size() < MIN_TDEE_DAYS || dailyCalories == null) {
            return estimated;
        }

        List<LocalDate> dates = new ArrayList<>(weightTrend.keySet());
        for (int i = MIN_TDEE_DAYS - 1; i < dates.size(); i++) {
            LocalDate date = dates.get(i);
            LocalDate prevDate = null;
            for (int j = i - 1; j >= 0; j--) {
                LocalDate past = dates.get(j);
                if (ChronoUnit.DAYS.between(past, date) >= 7) {
                    prevDate = past;
                    break;
                }
            }
            if (prevDate == null) {
                prevDate = dates.get(0);
            }

            Double currW = weightTrend.get(date);
            Double prevW = weightTrend.get(prevDate);
            if (currW == null || prevW == null) {
                continue;
            }

            double deltaWeight = currW - prevW;
            long actualDays = ChronoUnit.DAYS.between(prevDate, date);
            if (actualDays < 7 || actualDays > MetabolicConstants.MAX_GAP_DAYS) {
                Double fallbackTdee = bmrCalculatorService.computeInitialTdee(user, currW, latestBodyFat);
                if (fallbackTdee != null) {
                    estimated.put(date, fallbackTdee);
                }
                continue;
            }
            double deltaOver7Days = (deltaWeight / (double) actualDays) * 7.0;

            double caloriesSum = 0.0;
            int loggedDaysCount = 0;
            for (long d = 0; d < actualDays; d++) {
                LocalDate checkDate = date.minusDays(d);
                if (dailyCalories.containsKey(checkDate)) {
                    caloriesSum += dailyCalories.get(checkDate);
                    loggedDaysCount++;
                }
            }

            if (loggedDaysCount < Math.max(4, actualDays * 0.6)) continue;
            double loggedAverage = caloriesSum / loggedDaysCount;
            double estimatedActualAverage = loggedAverage;
            
            if (loggedDaysCount < actualDays) {
                double estimatedMissingCalories = (actualDays - loggedDaysCount) * (loggedAverage * MetabolicConstants.MISSING_DAYS_COEFF);
                estimatedActualAverage = (caloriesSum + estimatedMissingCalories) / actualDays;
            }

            double energyDensity = NutritionUtils.resolveEnergyDensity(deltaOver7Days, user.getTrainingType(), user.getTrainingExperience(), user.getSex());

            double tdee = estimatedActualAverage - (deltaOver7Days * energyDensity / 7.0);

            estimated.put(date, tdee);
        }

        List<LocalDate> estimatedDates = new ArrayList<>(estimated.keySet());
        Map<LocalDate, Double> smoothed = new LinkedHashMap<>();

        double tdeeAlpha = MetabolicConstants.EWMA_ALPHA_TDEE; 
        Double prevSmoothedTdee = null;

        for (LocalDate date : estimatedDates) {
            Double rawTdee = estimated.get(date);
            
            double clampedRawTdee = Math.max(MetabolicConstants.MIN_SURVIVAL_CALORIES, 
                                    Math.min(MetabolicConstants.MAX_TDEE_LIMIT, rawTdee));

            if (prevSmoothedTdee == null) {
                prevSmoothedTdee = clampedRawTdee;
            } else {
                prevSmoothedTdee = (tdeeAlpha * clampedRawTdee) + ((1.0 - tdeeAlpha) * prevSmoothedTdee);
            }
            
            smoothed.put(date, NumberUtils.roundToTwo(prevSmoothedTdee));
        }

        return smoothed;
    }

    public Map<LocalDate, Double> buildInitialTdeeLine(Collection<LocalDate> dates, double tdee) {
        Map<LocalDate, Double> result = new LinkedHashMap<>();
        if (dates == null) {
            return result;
        }
        for (LocalDate date : dates) {
            result.put(date, tdee);
        }
        return result;
    }


}