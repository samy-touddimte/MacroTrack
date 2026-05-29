package com.macrotrack.shared.util;

import com.macrotrack.user.model.BiologicalSex;
import com.macrotrack.user.model.TrainingExperience;
import com.macrotrack.user.model.TrainingType;

import static com.macrotrack.shared.util.NutritionalConstants.KCAL_PER_KG_FAT;
import static com.macrotrack.shared.util.NutritionalConstants.KCAL_PER_KG_LEAN_MASS;

public final class NutritionUtils {

    private NutritionUtils() {}

    /**
     * Calculates the weekly lean mass gain ceiling (kg/week)
     * based on training type, training experience, and sex.
     */
    public static double maxLeanGainPerWeek(
            TrainingType training,
            TrainingExperience experience,
            BiologicalSex sex
    ) {
        double trainingFactor = switch (training != null ? training : TrainingType.NONE) {
            case NONE        -> MetabolicConstants.LEAN_GAIN_FACTOR_NONE;
            case CARDIO_ONLY -> MetabolicConstants.LEAN_GAIN_FACTOR_CARDIO;
            case MIXED       -> MetabolicConstants.LEAN_GAIN_FACTOR_MIXED;
            case RESISTANCE  -> MetabolicConstants.LEAN_GAIN_FACTOR_RESISTANCE;
        };

        double experienceFactor = switch (experience != null ? experience : TrainingExperience.NOVICE) {
            case NOVICE       -> MetabolicConstants.LEAN_GAIN_FACTOR_NOVICE;
            case INTERMEDIATE -> MetabolicConstants.LEAN_GAIN_FACTOR_INTERMEDIATE;
            case ADVANCED     -> MetabolicConstants.LEAN_GAIN_FACTOR_ADVANCED;
            case ELITE        -> MetabolicConstants.LEAN_GAIN_FACTOR_ELITE;
        };

        double sexFactor = switch (sex != null ? sex : BiologicalSex.MALE) {
            case MALE   -> 1.0;
            case FEMALE -> MetabolicConstants.SEX_LEAN_GAIN_FACTOR_FEMALE;
            case OTHER  -> MetabolicConstants.SEX_LEAN_GAIN_FACTOR_OTHER;
        };

        return MetabolicConstants.MAX_LEAN_GAIN_WEEKLY_NOVICE_MALE_KG
                * trainingFactor
                * experienceFactor
                * sexFactor;
    }

    /**
     * Calculates the effective energy density of mass gain (kcal/kg).
     */
    public static double resolveGainEnergyDensity(
            double weeklyRateKg,
            TrainingType training,
            TrainingExperience experience,
            BiologicalSex sex
    ) {
        if (weeklyRateKg <= 0) {
            return KCAL_PER_KG_FAT;
        }

        double leanCeiling = maxLeanGainPerWeek(training, experience, sex);

        double leanGain = Math.min(weeklyRateKg, leanCeiling);
        double fatGain  = weeklyRateKg - leanGain;

        if (leanCeiling == 0.0) {
            return KCAL_PER_KG_FAT;
        }

        return (leanGain * KCAL_PER_KG_LEAN_MASS
              + fatGain  * KCAL_PER_KG_FAT)
              / weeklyRateKg;
    }

    /**
     * Calculates the effective energy density of mass loss (kcal/kg).
     * Models lean mass retention based on training type.
     */
    public static double resolveLossEnergyDensity(
            double weeklyRateKg,
            TrainingType training
    ) {
        if (weeklyRateKg >= 0) {
            return KCAL_PER_KG_FAT;
        }

        double leanLossRatio = switch (training != null ? training : TrainingType.NONE) {
            case RESISTANCE  -> 0.05;
            case MIXED       -> 0.15;
            default          -> 0.25;
        };

        double fatLossRatio = 1.0 - leanLossRatio;

        return (leanLossRatio * KCAL_PER_KG_LEAN_MASS
              + fatLossRatio * KCAL_PER_KG_FAT);
    }

    /**
     * Unified entry point for energy density,
     * covers both loss and gain.
     */
    public static double resolveEnergyDensity(
            double weeklyRate,
            TrainingType training,
            TrainingExperience experience,
            BiologicalSex sex
    ) {
        if (weeklyRate < 0) {
            return resolveLossEnergyDensity(weeklyRate, training);
        } else if (weeklyRate == 0) {
            return KCAL_PER_KG_FAT;
        }
        return resolveGainEnergyDensity(weeklyRate, training, experience, sex);
    }

}
