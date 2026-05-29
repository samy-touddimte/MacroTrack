package com.macrotrack.nutrition.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import java.time.LocalTime;

public record FoodLogResponse(
    Long id,
    LocalDate date,
    String foodName,
    Double caloriesKcal,
    Double proteinG,
    Double carbsG,
    Double fatG,
    @JsonFormat(pattern = "HH:mm")
    LocalTime loggedTime
) {}
