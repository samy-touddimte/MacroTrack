package com.macrotrack.analytics.tdee;

import com.macrotrack.analytics.tdee.TdeeResponse;
import com.macrotrack.user.model.User;
import java.time.LocalDate;

public interface TdeeEstimationService {
    TdeeResponse getTdee(String email, LocalDate from, LocalDate to);
}
