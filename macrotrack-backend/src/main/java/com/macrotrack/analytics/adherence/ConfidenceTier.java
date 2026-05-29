package com.macrotrack.analytics.adherence;

public enum ConfidenceTier {
    EXCELLENT(0.85, 5),
    GOOD(0.70, 4),
    FAIR(0.50, 3),
    POOR(0.30, 2),
    MINIMAL(0.0, 1);

    private final double minFillRate;
    private final int score;

    ConfidenceTier(double minFillRate, int score) {
        this.minFillRate = minFillRate;
        this.score = score;
    }

    public double getMinFillRate() {
        return minFillRate;
    }

    public int getScore() {
        return score;
    }
}
