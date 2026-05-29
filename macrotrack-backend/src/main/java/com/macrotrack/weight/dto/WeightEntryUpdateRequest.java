package com.macrotrack.weight.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import java.time.LocalTime;

public record WeightEntryUpdateRequest(
    @NotNull(message = "Logged time is required")
    @JsonFormat(pattern = "HH:mm")
    LocalTime loggedTime
) {}
