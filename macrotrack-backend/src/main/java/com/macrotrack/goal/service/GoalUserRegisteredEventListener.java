package com.macrotrack.goal.service;

import com.macrotrack.auth.dto.RegisterRequest;
import com.macrotrack.auth.event.UserRegisteredEvent;
import com.macrotrack.goal.dto.GoalRequest;
import com.macrotrack.user.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDate;

import org.springframework.core.annotation.Order;

@Component
@RequiredArgsConstructor
@Slf4j
public class GoalUserRegisteredEventListener {

    private final GoalService goalService;
    private final Clock clock;

    @EventListener
    @Order(2)
    public void handleUserRegisteredEvent(UserRegisteredEvent event) {
        User user = event.getUser();
        RegisterRequest request = event.getRegisterRequest();
        log.info("GoalUserRegisteredEventListener triggered for user {}", user.getEmail());
        if (request.targetWeightKg() != null && request.weeklyRateKg() != null) {
            GoalValidator.validateGoal(request.currentWeightKg(), request.targetWeightKg(), request.weeklyRateKg());
            GoalRequest goalRequest = new GoalRequest(
                    request.targetWeightKg(),
                    request.weeklyRateKg(),
                    LocalDate.now(clock)
            );
            goalService.createGoal(user.getEmail(), goalRequest);
            log.info("Created initial goal for newly registered user: {}", user.getEmail());
        }
    }
}
