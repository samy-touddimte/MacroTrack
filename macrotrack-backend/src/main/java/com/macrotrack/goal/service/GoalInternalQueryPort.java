package com.macrotrack.goal.service;

import com.macrotrack.goal.dto.GoalResponse;
import com.macrotrack.user.model.User;

import java.util.Optional;

public interface GoalInternalQueryPort {
    Optional<GoalResponse> findActiveGoal(User user);
}
