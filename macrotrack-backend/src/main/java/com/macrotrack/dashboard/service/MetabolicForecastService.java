package com.macrotrack.dashboard.service;

import com.macrotrack.analytics.projection.MetabolicForecastResponse;
import com.macrotrack.analytics.projection.SimulationContext;
import com.macrotrack.user.model.User;
import com.macrotrack.analytics.projection.WeightProjectionEngine;
import com.macrotrack.analytics.bmr.BmrCalculatorService;
import com.macrotrack.analytics.tdee.TdeeInternalQueryPort;
import com.macrotrack.user.service.UserInternalQueryPort;
import com.macrotrack.weight.service.WeightEntryService;
import com.macrotrack.shared.util.MetabolicConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Slf4j
public class MetabolicForecastService {

    private final UserInternalQueryPort userService;
    private final WeightProjectionEngine weightProjectionEngine;
    private final BmrCalculatorService bmrCalculatorService;
    private final TdeeInternalQueryPort tdeeEstimationService;
    private final WeightEntryService weightEntryService;
    private final Clock clock;

    public MetabolicForecastResponse getMetabolicForecast(
            String email,
            double currentWeight,
            double goalWeight,
            double weeklyRate,
            LocalDate startDate
    ) {
        User user = userService.getUserEntityByEmail(email);
        Double latestBodyFat = weightEntryService.getLatestBodyFatPercentage(user);
        
        Double currentTdee = tdeeEstimationService.effectiveTdee(user, LocalDate.now(clock));
        
        if (currentTdee == null) {
            currentTdee = bmrCalculatorService.computeInitialTdee(user, currentWeight, latestBodyFat);
            if (currentTdee == null) {
                currentTdee = MetabolicConstants.DEFAULT_TDEE_KCAL;
            }
        }

        Double mifflinNow = bmrCalculatorService.computeInitialTdee(user, currentWeight, latestBodyFat);
        if (mifflinNow == null) {
            log.warn("Cannot compute initial TDEE (Mifflin) for user {}. Adaptation will be ignored (0).", user.getId());
        }
        double adaptation = currentTdee - (mifflinNow != null ? mifflinNow : currentTdee);

        var forecast = weightProjectionEngine.generateWeightProjection(
                new SimulationContext(
                        currentWeight,
                        goalWeight,
                        weeklyRate,
                        startDate,
                        currentTdee,
                        user,
                        adaptation,
                        currentWeight,
                        0,
                        latestBodyFat
                )
        );

        return new MetabolicForecastResponse(forecast.points(), forecast.estimatedReachDate(), forecast.extremeDeficit());
    }
}
