package com.macrotrack.shared.util;

public final class MetabolicConstants {
    private MetabolicConstants() {}

    public static final double MIN_SURVIVAL_CALORIES = 800.0;
    public static final double MIN_CLINICAL_CALORIES_FEMALE = 1000.0;
    public static final double MIN_CLINICAL_CALORIES_MALE = 1000.0;
    public static final double MAX_TDEE_LIMIT = 6000.0;
    public static final double DEFAULT_TDEE_KCAL = 2000.0;



    /**
     * Fraction of initial body weight lost before adaptive thermogenesis becomes measurable.
     * Set to 0.05 (5%) based on Rosenbaum & Leibel (2010) — metabolic adaptation is consistently
     * observed beyond this threshold in clinical studies. The original 0.02 (2%) was too early
     * and caused near-zero penalties that were scientifically misleading.
     */
    public static final double ADAPT_THERMO_START = 0.05;
    public static final double ADAPT_THERMO_FULL = 0.15;
    public static final double ADAPT_MAX_PENALTY = 0.125;

    public static final int NEAT_ONSET_DAYS = 21;
    public static final int NEAT_RAMP_DAYS = 28;
    public static final double NEAT_MAX_BOOST = 0.10;

    public static final double MAX_LEAN_GAIN_WEEKLY_NOVICE_MALE_KG = 0.25;

    public static final double SEX_LEAN_GAIN_FACTOR_FEMALE = 0.55;
    public static final double SEX_LEAN_GAIN_FACTOR_OTHER = 0.75;

    public static final double LEAN_GAIN_FACTOR_RESISTANCE = 1.00;
    public static final double LEAN_GAIN_FACTOR_MIXED      = 0.60;
    public static final double LEAN_GAIN_FACTOR_CARDIO     = 0.20;
    public static final double LEAN_GAIN_FACTOR_NONE       = 0.00;

    public static final double LEAN_GAIN_FACTOR_NOVICE       = 1.00;
    public static final double LEAN_GAIN_FACTOR_INTERMEDIATE = 0.50;
    public static final double LEAN_GAIN_FACTOR_ADVANCED     = 0.25;
    public static final double LEAN_GAIN_FACTOR_ELITE        = 0.10;

    /**
     * Multipliers for initial water/glycogen weight variation.
     * Water loss (1.2) is generally faster/larger than initial gain (0.5).
     */
    public static final double WATER_WEIGHT_LOSS_MULTIPLIER = 1.2;
    public static final double WATER_WEIGHT_GAIN_MULTIPLIER = 0.5;
    
    public static final double SCENARIO_MODERATE_MULTIPLIER = 0.8;
    public static final double SCENARIO_CONSERVATIVE_MULTIPLIER = 0.6;
    
    public static final int TDEE_FULL_CONFIDENCE_DAYS = 21;
    public static final double ADHERENCE_TOLERANCE_MIN = 100.0;
    public static final double ADHERENCE_TOLERANCE_MAX = 250.0;
    public static final int MAX_PROJECTION_DAYS = 730;
    public static final double EWMA_ALPHA = 0.1;
    /**
     * Threshold (21 days) matching the NEAT adaptation timeframe.
     * Provides a safe buffer for missing data before forcing a fallback.
     */
    public static final long MAX_GAP_DAYS = 21;
    public static final int TDEE_TREND_WINDOW_DAYS = 45;
    public static final double EWMA_ALPHA_TDEE = 0.15;
    public static final int TDEE_AVERAGING_WINDOW_DAYS = 14;
    
    /**
     * Coefficient to estimate caloric intake on unlogged days (1.0 = same as logged average).
     * NOTE: Research suggests people tend NOT to log on days of higher intake (dietary recall bias).
     * A coefficient of 1.1-1.2 would be more realistic. 1.0 is the conservative default.
     */
    public static final double MISSING_DAYS_COEFF = 1.0;
    
    /**
     * Alpha blending factor used in smoothing algorithms (e.g., metabolic adaptation phases)
     * to transition between values. 0.30 favors historical trend with moderate responsiveness to new data.
     */
    public static final double PHASE_BONUS_ALPHA = 0.30;
}
