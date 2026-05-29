package com.macrotrack.auth.controller;

import com.macrotrack.auth.dto.LoginRequest;
import com.macrotrack.auth.dto.RefreshRequest;
import com.macrotrack.auth.dto.RegisterRequest;
import com.macrotrack.auth.dto.ChangePasswordRequest;
import com.macrotrack.auth.dto.AuthResponse;
import com.macrotrack.auth.service.AuthService;
import com.macrotrack.auth.security.CurrentUserEmail;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "Registration, login and token management")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Register a new user")
    public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    @Operation(summary = "Log in")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh JWT token")
    public AuthResponse refreshToken(@Valid @RequestBody RefreshRequest request) {
        return authService.refreshToken(request.refreshToken());
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Log out")
    public void logout(@Valid @RequestBody RefreshRequest request) {
        authService.logout(request.refreshToken());
    }

    @PostMapping("/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Change password")
    public void changePassword(@Valid @RequestBody ChangePasswordRequest request, @CurrentUserEmail String email) {
        authService.changePassword(email, request.currentPassword(), request.newPassword());
    }
}
