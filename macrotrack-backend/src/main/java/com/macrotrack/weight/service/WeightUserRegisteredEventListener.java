package com.macrotrack.weight.service;

import com.macrotrack.auth.dto.RegisterRequest;
import com.macrotrack.auth.event.UserRegisteredEvent;
import com.macrotrack.user.model.User;
import com.macrotrack.weight.dto.WeightEntryRequest;
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
public class WeightUserRegisteredEventListener {

    private final WeightEntryService weightEntryService;
    private final Clock clock;

    @EventListener
    @Order(1)
    public void handleUserRegisteredEvent(UserRegisteredEvent event) {
        User user = event.getUser();
        RegisterRequest request = event.getRegisterRequest();
        log.info("WeightUserRegisteredEventListener triggered for user {}", user.getEmail());
        if (request.currentWeightKg() != null) {
            WeightEntryRequest weightRequest = new WeightEntryRequest(
                    LocalDate.now(clock),
                    request.currentWeightKg(),
                    request.bodyFatPercentage(),
                    null
            );
            weightEntryService.addOrUpdateEntry(user.getEmail(), weightRequest);
            log.info("Created initial weight entry for newly registered user: {}", user.getEmail());
        }
    }
}
