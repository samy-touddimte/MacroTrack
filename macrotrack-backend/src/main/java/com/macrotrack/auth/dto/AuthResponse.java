package com.macrotrack.auth.dto;

public record AuthResponse(
    String accessToken,
    String refreshToken,
    String username,
    String email
) {}
