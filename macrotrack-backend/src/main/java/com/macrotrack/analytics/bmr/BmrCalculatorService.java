package com.macrotrack.analytics.bmr;
import com.macrotrack.analytics.bmr.BmrFormula;

import com.macrotrack.user.model.ActivityLevel;
import com.macrotrack.user.model.User;
import com.macrotrack.shared.util.MetabolicConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BmrCalculatorService {

    private final List<BmrFormula> formulas;

    public Double computeInitialTdee(User user, Double weightKg, Double latestBodyFat) {
        if (user == null || weightKg == null) {
            return null;
        }

        double calculatedBmr = 0;
        boolean formulaApplied = false;

        for (BmrFormula formula : formulas) {
            if (formula.isApplicable(user, latestBodyFat)) {
                calculatedBmr = formula.calculateBmr(user, weightKg, latestBodyFat);
                formulaApplied = true;
                break;
            }
        }

        if (!formulaApplied) {
            Object userIdLog = user.getId() != null ? user.getId() : "unpersisted";
            log.info("Incomplete profile for user {}, using default TDEE: {} kcal", userIdLog, MetabolicConstants.DEFAULT_TDEE_KCAL);
            return MetabolicConstants.DEFAULT_TDEE_KCAL;
        }

        ActivityLevel activityLevel = user.getActivityLevel();
        if (activityLevel == null) {
            Object userIdLog = user.getId() != null ? user.getId() : "unpersisted";
            log.info("Activity level is null for user {}, falling back to SEDENTARY. TDEE may be underestimated.", userIdLog);
            activityLevel = ActivityLevel.SEDENTARY;
        }
        return calculatedBmr * activityLevel.getPalMultiplier();
    }
}
