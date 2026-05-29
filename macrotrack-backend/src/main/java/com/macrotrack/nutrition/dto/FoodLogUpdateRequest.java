package com.macrotrack.nutrition.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import java.time.LocalTime;

public record FoodLogUpdateRequest(
    @NotNull(message = "Logged time is required")
    @JsonFormat(pattern = "HH:mm")
    LocalTime loggedTime
) {}
