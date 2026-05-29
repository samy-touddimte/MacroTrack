package com.macrotrack.goal.dto;

import java.time.LocalDate;

public record GoalResponse(
    Long id,
    Double targetWeightKg,
    Double weeklyRateKg,
    LocalDate startDate,
    Boolean active
) {}
