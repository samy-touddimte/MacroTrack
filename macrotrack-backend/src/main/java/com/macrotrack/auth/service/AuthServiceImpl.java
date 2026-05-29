package com.macrotrack.auth.service;

import com.macrotrack.auth.dto.LoginRequest;
import com.macrotrack.auth.dto.RegisterRequest;
import com.macrotrack.auth.dto.AuthResponse;
import com.macrotrack.user.repository.UserRepository;
import com.macrotrack.auth.security.JwtUtils;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import com.macrotrack.user.model.User;
import com.macrotrack.auth.event.UserRegisteredEvent;
import org.springframework.context.ApplicationEventPublisher;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import com.macrotrack.auth.model.RefreshToken;
import java.time.Clock;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final RefreshTokenService refreshTokenService;
    private final ApplicationEventPublisher eventPublisher;

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already in use");
        }

        User user = User.builder()
                .email(request.email())
                .username(request.username())
                .passwordHash(passwordEncoder.encode(request.password()))
                .heightCm(request.heightCm())
                .birthDate(request.birthDate())
                .sex(request.sex())
                .activityLevel(request.activityLevel())
                .trainingType(request.trainingType())
                .trainingExperience(request.trainingExperience())
                .bodyFatPercentage(request.bodyFatPercentage())
                .build();
        User saved = userRepository.save(user);

        eventPublisher.publishEvent(new UserRegisteredEvent(this, saved, request));

        return buildAuthResponse(saved);
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password"));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
        }

        return buildAuthResponse(user);
    }

    public AuthResponse refreshToken(String refreshTokenStr) {
        if (refreshTokenStr == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing token");
        }

        RefreshToken storedToken = refreshTokenService.validateAndGetToken(refreshTokenStr);
        User user = storedToken.getUser();
        String email = user.getEmail();
        if (email == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token");
        }

        storedToken.setRevoked(true);
        refreshTokenService.save(storedToken);

        return buildAuthResponse(user);
    }

    public void logout(String refreshTokenStr) {
        if (refreshTokenStr == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing token");
        }
        refreshTokenService.revokeToken(refreshTokenStr);
    }

    public void changePassword(String email, String currentPassword, String newPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        
        if (!passwordEncoder.matches(currentPassword, user.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Current password is incorrect");
        }
        
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        log.warn("Security audit: password changed for user {}", email);
        refreshTokenService.revokeAllUserTokens(user.getId());
    }

    private AuthResponse buildAuthResponse(User user) {
        String accessToken = jwtUtils.generateAccessToken(user.getEmail());
        String refreshTokenStr = jwtUtils.generateRefreshToken(user.getEmail());
        refreshTokenService.persistRefreshToken(user, refreshTokenStr);

        return new AuthResponse(
            accessToken,
            refreshTokenStr,
            user.getUsername(),
            user.getEmail()
        );
    }
}
