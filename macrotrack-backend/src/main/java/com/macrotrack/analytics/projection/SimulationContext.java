package com.macrotrack.analytics.projection;

import com.macrotrack.user.model.User;
import java.time.LocalDate;

public record SimulationContext(
        double currentWeight,
        double goalWeight,
        double weeklyRate,
        LocalDate startDate,
        double baseTdee,
        User user,
        double measuredAdaptation,
        double trueInitialWeight,
        int daysElapsedAlready,
        Double latestBodyFat
) {}
