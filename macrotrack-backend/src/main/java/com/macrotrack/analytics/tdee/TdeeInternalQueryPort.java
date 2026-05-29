package com.macrotrack.analytics.tdee;

import com.macrotrack.user.model.User;
import java.time.LocalDate;

public interface TdeeInternalQueryPort {
    Double effectiveTdee(User user, LocalDate targetDate);
}
