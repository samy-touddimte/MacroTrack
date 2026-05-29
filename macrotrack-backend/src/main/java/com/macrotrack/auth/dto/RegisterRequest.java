package com.macrotrack.auth.dto;

import jakarta.validation.constraints.*;
import com.macrotrack.user.model.ActivityLevel;
import com.macrotrack.user.model.BiologicalSex;
import com.macrotrack.user.model.TrainingType;
import com.macrotrack.user.model.TrainingExperience;
import com.macrotrack.validation.ValidAge;
import com.macrotrack.validation.ValidPassword;
import java.time.LocalDate;

public record RegisterRequest(
    @Email @NotBlank String email,
    @NotBlank String username,
    @ValidPassword String password,
    @NotNull(message = "Height is required")
    @DecimalMin(value = "100.0", message = "Height must be at least 100 cm") @DecimalMax(value = "250.0", message = "Height cannot exceed 250 cm") Double heightCm,
    
    // Optional: users might not know their body fat percentage initially
    @DecimalMin(value = "3.0", message = "Body fat percentage cannot be lower than 3%") @DecimalMax(value = "60.0", message = "Body fat percentage cannot exceed 60%") Double bodyFatPercentage,
    
    @NotNull(message = "Birth date is required")
    @Past(message = "Birth date must be in the past") @ValidAge(min = 13, max = 120, message = "Age must be between 13 and 120 years") LocalDate birthDate,
    
    @NotNull(message = "Sex is required") BiologicalSex sex,
    
    @NotNull(message = "Activity level is mandatory")
    ActivityLevel activityLevel,
    
    // Optional: training specifics can be added later
    TrainingType trainingType,
    TrainingExperience trainingExperience,
    
    @NotNull(message = "Current weight is required")
    @DecimalMin(value = "30.0", message = "Current weight must be at least 30 kg") @DecimalMax(value = "300.0", message = "Current weight cannot exceed 300 kg") Double currentWeightKg,
    
    // Optional: users might only want to track without a specific goal
    @DecimalMin(value = "30.0", message = "Target weight must be at least 30 kg") @DecimalMax(value = "300.0", message = "Target weight cannot exceed 300 kg") Double targetWeightKg,
    @DecimalMin(value = "-1.5", message = "Weekly rate must be between -1.5 and 1.5 kg/week") @DecimalMax(value = "1.5", message = "Weekly rate must be between -1.5 and 1.5 kg/week") Double weeklyRateKg
) {}
