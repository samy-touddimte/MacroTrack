package com.macrotrack.user.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import com.macrotrack.user.model.ActivityLevel;
import com.macrotrack.user.model.BiologicalSex;
import com.macrotrack.user.model.TrainingType;
import com.macrotrack.user.model.TrainingExperience;

public record UpdateUserRequest(
    @DecimalMin(value = "100.0", message = "Height must be at least 100 cm") 
    @DecimalMax(value = "250.0", message = "Height cannot exceed 250 cm") 
    Double heightCm,
    
    @DecimalMin(value = "3.0", message = "Body fat percentage cannot be lower than 3%") 
    @DecimalMax(value = "60.0", message = "Body fat percentage cannot exceed 60%") 
    Double bodyFatPercentage,
    
    ActivityLevel activityLevel,
    
    TrainingType trainingType,
    TrainingExperience trainingExperience,
    @Size(min = 2, max = 50, message = "Username must be between 2 and 50 characters")
    String username,
    
    @Past(message = "Birth date must be in the past") 
    @com.macrotrack.validation.ValidAge(min = 13, max = 120, message = "Age must be between 13 and 120 years")
    java.time.LocalDate birthDate,
    
    BiologicalSex sex
) {}
