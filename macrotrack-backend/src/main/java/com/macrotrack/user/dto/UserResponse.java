package com.macrotrack.user.dto;

import com.macrotrack.user.model.ActivityLevel;
import com.macrotrack.user.model.BiologicalSex;
import com.macrotrack.user.model.TrainingType;
import com.macrotrack.user.model.TrainingExperience;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record UserResponse(
    Long id,
    String email,
    String username,
    Double heightCm,
    Double bodyFatPercentage,
    LocalDate birthDate,
    BiologicalSex sex,
    ActivityLevel activityLevel,
    TrainingType trainingType,
    TrainingExperience trainingExperience,
    java.time.Instant createdAt
) {}
