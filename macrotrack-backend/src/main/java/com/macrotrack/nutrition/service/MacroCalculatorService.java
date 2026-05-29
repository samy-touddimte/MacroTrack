package com.macrotrack.nutrition.service;

import com.macrotrack.nutrition.dto.MacroTargets;
import com.macrotrack.user.model.User;
import com.macrotrack.shared.util.NumberUtils;
import com.macrotrack.shared.util.NutritionUtils;
import com.macrotrack.shared.util.NutritionalConstants;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class MacroCalculatorService {

    private static final double MIN_CARBS_G = 130.0;
    private static final double MIN_FAT_MULTIPLIER = 0.8;



    public MacroTargets computeMacroTargets(double effectiveWeight, Double bodyFatPercentage, double dailyCalorieTarget, double weeklyRateKg) {
        
        double proteinPerKg = (weeklyRateKg < 0) ? NutritionalConstants.PROTEIN_PER_KG_LOSS : (weeklyRateKg > 0 ? NutritionalConstants.PROTEIN_PER_KG_GAIN : NutritionalConstants.PROTEIN_PER_KG_MAINTAIN);
        
        double leanBodyMass = effectiveWeight;
        if (bodyFatPercentage != null && bodyFatPercentage > 0) {
            leanBodyMass = effectiveWeight * (1.0 - (bodyFatPercentage / 100.0));
        }
        
        double targetProtein = leanBodyMass * proteinPerKg;
        
        double baseFatPerKg = NutritionalConstants.FAT_PER_KG_BASE;
        double targetFat = effectiveWeight * baseFatPerKg;

        double proteinCals = targetProtein * NutritionalConstants.KCAL_PER_GRAM_PROTEIN;
        double fatCals = targetFat * NutritionalConstants.KCAL_PER_GRAM_FAT;
        double remainingCals = dailyCalorieTarget - (proteinCals + fatCals);

        double targetCarbs;

        if (remainingCals < 0) {
            double minimumVitalFat = effectiveWeight * MIN_FAT_MULTIPLIER;
            
            targetFat = Math.max(minimumVitalFat, (dailyCalorieTarget - proteinCals) / NutritionalConstants.KCAL_PER_GRAM_FAT);
            
            targetCarbs = Math.max(MIN_CARBS_G, (dailyCalorieTarget - (proteinCals + (targetFat * NutritionalConstants.KCAL_PER_GRAM_FAT))) / NutritionalConstants.KCAL_PER_GRAM_CARBS);
            
        } else {
            targetCarbs = Math.max(MIN_CARBS_G, remainingCals / NutritionalConstants.KCAL_PER_GRAM_CARBS);
        }

        double totalMacroCals = targetProtein * NutritionalConstants.KCAL_PER_GRAM_PROTEIN + 
                                targetFat * NutritionalConstants.KCAL_PER_GRAM_FAT + 
                                targetCarbs * NutritionalConstants.KCAL_PER_GRAM_CARBS;
        
        if (totalMacroCals > dailyCalorieTarget + 10) { // 10 kcal tolerance
            log.warn("Macro calculation fallback triggered: Minimum physiological macro requirements ({} kcal) exceed the allocated daily budget ({} kcal).", 
                     NumberUtils.roundToTwo(totalMacroCals), NumberUtils.roundToTwo(dailyCalorieTarget));
        }

        return new MacroTargets(
            NumberUtils.roundToTwo(targetProtein),
            NumberUtils.roundToTwo(targetFat),
            NumberUtils.roundToTwo(targetCarbs)
        );
    }

    public Double computeDailyCalorieTarget(User user, double currentTdee, double weeklyRate) {
        double energyDensity = NutritionUtils.resolveEnergyDensity(weeklyRate, user.getTrainingType(), user.getTrainingExperience(), user.getSex());
        return currentTdee + (weeklyRate * energyDensity / 7.0);
    }
}