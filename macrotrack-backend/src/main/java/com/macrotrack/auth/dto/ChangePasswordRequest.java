package com.macrotrack.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import com.macrotrack.validation.ValidPassword;

public record ChangePasswordRequest(
    @NotBlank(message = "Current password is required")
    String currentPassword,
    @ValidPassword
    String newPassword
) {}
