package com.macrotrack.nutrition.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Max;
import java.time.LocalDate;
import java.time.LocalTime;

public record FoodLogRequest(
    @NotNull(message = "Date is required") 
    @PastOrPresent(message = "Date cannot be in the future") 
    LocalDate date,
    
    @NotBlank(message = "Food name is required") 
    @Size(max = 200, message = "Food name cannot exceed 200 characters")
    String foodName,
    
    @NotNull(message = "Calories are required")
    @Min(value = 0, message = "Calories cannot be negative")
    @Max(value = 5000, message = "Calories cannot exceed 5000")
    Double caloriesKcal,
    
    @Min(value = 0, message = "Protein cannot be negative") Double proteinG,
    @Min(value = 0, message = "Carbs cannot be negative") Double carbsG,
    @Min(value = 0, message = "Fat cannot be negative") Double fatG,

    @JsonFormat(pattern = "HH:mm")
    LocalTime loggedTime
) {}
