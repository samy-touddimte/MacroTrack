package com.macrotrack.auth.service;

import com.macrotrack.auth.dto.LoginRequest;
import com.macrotrack.auth.dto.RegisterRequest;
import com.macrotrack.auth.dto.AuthResponse;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    AuthResponse refreshToken(String refreshTokenStr);
    void logout(String refreshTokenStr);
    void changePassword(String email, String currentPassword, String newPassword);
}
