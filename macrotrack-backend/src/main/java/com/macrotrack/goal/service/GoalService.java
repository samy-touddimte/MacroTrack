package com.macrotrack.goal.service;

import com.macrotrack.goal.dto.GoalRequest;
import com.macrotrack.goal.dto.GoalResponse;
import java.util.Optional;

public interface GoalService {
    GoalResponse createGoal(String email, GoalRequest request);
    GoalResponse getActiveGoal(String email);
    Optional<GoalResponse> findActiveGoalByEmail(String email);
    GoalResponse updateGoal(String email, Long goalId, GoalRequest request);
    java.util.List<GoalResponse> getGoalHistory(String email);
    void deleteGoal(String email, Long goalId);
}
