package com.macrotrack.weight.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public record WeightEntryResponse(
    Long id,
    LocalDate date,
    Double weightKg,
    Double bodyFatPercentage,
    java.time.Instant createdAt,
    @JsonFormat(pattern = "HH:mm")
    LocalTime loggedTime
) {}
