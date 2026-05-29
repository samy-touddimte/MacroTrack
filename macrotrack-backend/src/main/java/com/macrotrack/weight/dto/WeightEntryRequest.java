package com.macrotrack.weight.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import java.time.LocalDate;
import java.time.LocalTime;

public record WeightEntryRequest(
    @NotNull(message = "Date is required") 
    @PastOrPresent(message = "Date cannot be in the future") 
    LocalDate date,
    
    @NotNull(message = "Weight is required") 
    @DecimalMin(value = "30.0", message = "Weight must be at least 30 kg") 
    @DecimalMax(value = "300.0", message = "Weight cannot exceed 300 kg") 
    Double weightKg,

    @DecimalMin(value = "3.0", message = "Body fat must be at least 3.0%")
    @DecimalMax(value = "60.0", message = "Body fat cannot exceed 60.0%")
    Double bodyFatPercentage,

    @JsonFormat(pattern = "HH:mm")
    LocalTime loggedTime
) {}
