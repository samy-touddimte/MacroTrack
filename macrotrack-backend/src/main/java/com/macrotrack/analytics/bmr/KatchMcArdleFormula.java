package com.macrotrack.analytics.bmr;

import com.macrotrack.user.model.BiologicalSex;
import com.macrotrack.user.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.core.annotation.Order;

@Component
@Order(1)
@Slf4j
public class KatchMcArdleFormula implements BmrFormula {

    @Override
    public boolean isApplicable(User user, Double latestBodyFat) {
        return latestBodyFat != null || user.getBodyFatPercentage() != null;
    }

    @Override
    public double calculateBmr(User user, double weightKg, Double latestBodyFat) {
        double bf = latestBodyFat != null ? latestBodyFat : user.getBodyFatPercentage();
        double minBf = 3.0; // Default safe minimum
        if (user.getSex() != null) {
            switch (user.getSex()) {
                case FEMALE -> minBf = 10.0;
                case MALE -> minBf = 3.0;
                case OTHER -> minBf = 3.0; // Fallback to male physiological minimum for safety
            }
        }
        
        if (bf < minBf) {
            log.warn("Body fat {}% below physiological minimum for sex={}, userId={}. Clamped to {}%.", bf, user.getSex(), user.getId() != null ? user.getId() : "unpersisted", minBf);
            bf = minBf;
        }
        
        double leanBodyMass = weightKg * (1.0 - (bf / 100.0));
        return 370.0 + (21.6 * leanBodyMass);
    }
}
