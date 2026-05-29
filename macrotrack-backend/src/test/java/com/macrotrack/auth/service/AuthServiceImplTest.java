package com.macrotrack.auth.service;

import com.macrotrack.auth.dto.AuthResponse;
import com.macrotrack.auth.dto.LoginRequest;
import com.macrotrack.auth.dto.RegisterRequest;
import com.macrotrack.user.model.User;
import com.macrotrack.user.repository.UserRepository;
import java.time.LocalDate;
import com.macrotrack.user.model.BiologicalSex;
import com.macrotrack.user.model.ActivityLevel;
import org.springframework.context.ApplicationEventPublisher;
import com.macrotrack.auth.event.UserRegisteredEvent;
import com.macrotrack.auth.security.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;


import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private AuthServiceImpl authService;

    @BeforeEach
    void setUp() {
    }

    @Test
    @DisplayName("register_existingEmail_throwsBadRequest")
    void register_existingEmail_throwsBadRequest() {
        when(userRepository.existsByEmail("test@test.com")).thenReturn(true);
        RegisterRequest req = new RegisterRequest("test@test.com", "Test", "Pass123!", 180.0, 15.0, LocalDate.of(1990, 1, 1), BiologicalSex.MALE, ActivityLevel.SEDENTARY, null, null, 80.0, 75.0, -0.5);
        assertThrows(ResponseStatusException.class, () -> authService.register(req));
    }

    @Test
    @DisplayName("register_validRequest_savesUserAndPublishesEvent")
    void register_validRequest_savesUserAndPublishesEvent() {
        when(userRepository.existsByEmail("test@test.com")).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("hashed");
        
        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setEmail("test@test.com");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        
        when(jwtUtils.generateAccessToken(anyString())).thenReturn("jwt");
        when(jwtUtils.generateRefreshToken(anyString())).thenReturn("refresh");

        RegisterRequest req = new RegisterRequest("test@test.com", "Test", "Pass123!", 180.0, 15.0, LocalDate.of(1990, 1, 1), BiologicalSex.MALE, ActivityLevel.SEDENTARY, null, null, 80.0, 75.0, -0.5);
        
        AuthResponse resp = authService.register(req);
        
        assertNotNull(resp);
        assertEquals("jwt", resp.accessToken());
        verify(userRepository).save(any(User.class));
        verify(eventPublisher).publishEvent(any(UserRegisteredEvent.class));
    }

    @Test
    @DisplayName("login_wrongPassword_throwsUnauthorized")
    void login_wrongPassword_throwsUnauthorized() {
        User user = new User();
        user.setPasswordHash("hash");
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "hash")).thenReturn(false);
        
        LoginRequest req = new LoginRequest("test@test.com", "wrong");
        assertThrows(ResponseStatusException.class, () -> authService.login(req));
    }

    @Test
    @DisplayName("login_validCredentials_returnsTokens")
    void login_validCredentials_returnsTokens() {
        User user = new User();
        user.setId(1L);
        user.setEmail("test@test.com");
        user.setPasswordHash("hash");
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("pass", "hash")).thenReturn(true);
        when(jwtUtils.generateAccessToken(anyString())).thenReturn("jwt");
        when(jwtUtils.generateRefreshToken(anyString())).thenReturn("refresh");
        
        LoginRequest req = new LoginRequest("test@test.com", "pass");
        AuthResponse resp = authService.login(req);
        
        assertNotNull(resp);
        assertEquals("jwt", resp.accessToken());
        assertEquals("refresh", resp.refreshToken());
        verify(refreshTokenService).persistRefreshToken(eq(user), eq("refresh"));
    }

    @Test
    @DisplayName("logout_callsRevokeToken")
    void logout_callsRevokeToken() {
        authService.logout("refresh");
        verify(refreshTokenService).revokeToken("refresh");
    }
}
