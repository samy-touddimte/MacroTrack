package com.macrotrack.analytics.bmr;

import com.macrotrack.user.model.User;

public interface BmrFormula {
    
    boolean isApplicable(User user, Double latestBodyFat);
    
    double calculateBmr(User user, double weightKg, Double latestBodyFat);
}
