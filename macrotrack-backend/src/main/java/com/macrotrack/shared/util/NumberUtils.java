package com.macrotrack.shared.util;

public final class NumberUtils {
    
    private NumberUtils() {
        // Private constructor for utility class
    }

    public static double roundToTwo(double value) {
        if (!Double.isFinite(value)) {
            return value;
        }
        return Math.round(value * 100.0) / 100.0;
    }
}