package com.macrotrack.analytics.bmr;

import com.macrotrack.user.model.BiologicalSex;
import com.macrotrack.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDate;
import java.time.Period;

import org.springframework.core.annotation.Order;
import lombok.extern.slf4j.Slf4j;

@Component
@Order(2)
@RequiredArgsConstructor
@Slf4j
public class MifflinStJeorFormula implements BmrFormula {

    private final Clock clock;

    @Override
    public boolean isApplicable(User user, Double latestBodyFat) {
        return user.getHeightCm() != null && user.getBirthDate() != null && user.getSex() != null;
    }

    @Override
    public double calculateBmr(User user, double weightKg, Double latestBodyFat) {
        int sexModifier;
        if (user.getSex() == BiologicalSex.MALE) {
            sexModifier = 5;
        } else if (user.getSex() == BiologicalSex.FEMALE) {
            sexModifier = -161;
        /**
         * Sex modifier for BiologicalSex.OTHER: pragmatic midpoint between MALE (+5) and FEMALE (-161).
         * Value: (-161 + 5) / 2 = -78. No direct clinical basis — used as a reasonable approximation
         * when the user's biological sex is not binary. Documented limitation.
         */
        } else { // OTHER
            sexModifier = -78;
        }
        
        int age = Period.between(user.getBirthDate(), LocalDate.now(clock)).getYears();
        if (age <= 0) {
            log.warn("Non-positive age computed for userId={}, birthDate={}. Using age=1 as fallback.", 
                     user.getId() != null ? user.getId() : "unpersisted", user.getBirthDate());
            age = 1;
        }
        
        return 10 * weightKg + 6.25 * user.getHeightCm() - 5 * age + sexModifier;
    }
}
