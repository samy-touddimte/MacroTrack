package com.macrotrack.goal.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.PastOrPresent;
import java.time.LocalDate;

public record GoalRequest(
    @NotNull(message = "Target weight is required") 
    @DecimalMin(value = "30.0", message = "Target weight must be at least 30 kg") 
    @DecimalMax(value = "300.0", message = "Target weight cannot exceed 300 kg") 
    Double targetWeightKg,
    
    @NotNull(message = "Weekly loss rate is required") 
    @DecimalMin(value = "-1.5", message = "Weekly rate must be between -1.5 and 1.5 kg/week") 
    @DecimalMax(value = "1.5", message = "Weekly rate must be between -1.5 and 1.5 kg/week") 
    Double weeklyRateKg,
    
    @PastOrPresent(message = "Start date cannot be in the future")
    LocalDate startDate
) {}
