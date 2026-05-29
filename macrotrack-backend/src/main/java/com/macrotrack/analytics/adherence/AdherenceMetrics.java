package com.macrotrack.analytics.adherence;

public record AdherenceMetrics(
        double adherenceScore, 
        double adherence7d,
        double adherence14d,
        double adherence30d,
        boolean insufficientData,
        double loggedAdherence
) {
    public static AdherenceMetrics insufficient() {
        return new AdherenceMetrics(
                0.0, 0.0, 0.0, 0.0, 
                true, 0.0
        );
    }
}