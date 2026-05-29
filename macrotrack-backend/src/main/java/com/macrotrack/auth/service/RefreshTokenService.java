package com.macrotrack.auth.service;

import com.macrotrack.auth.model.RefreshToken;
import com.macrotrack.user.model.User;
import com.macrotrack.auth.repository.RefreshTokenRepository;
import com.macrotrack.auth.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Clock;
import java.time.LocalDateTime;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
@Transactional
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtUtils jwtUtils;
    private final Clock clock;

    private String hashToken(String token) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(token.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder(2 * hash.length);
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to hash token", e);
        }
    }

    public void revokeAllUserTokens(Long userId) {
        refreshTokenRepository.revokeAllUserTokens(userId);
    }

    public void persistRefreshToken(User user, String refreshToken) {
        RefreshToken rt = RefreshToken.builder()
            .token(hashToken(refreshToken))
            .user(user)
            .expiryDate(LocalDateTime.now(clock).plusNanos(jwtUtils.getRefreshExpirationMs() * 1_000_000L))
            .revoked(false)
            .build();
        refreshTokenRepository.save(rt);
    }

    public void revokeToken(String tokenStr) {
        refreshTokenRepository.findByToken(hashToken(tokenStr))
            .ifPresent(token -> {
                token.setRevoked(true);
                refreshTokenRepository.save(token);
            });
    }

    public RefreshToken validateAndGetToken(String tokenStr) {
        RefreshToken storedToken = refreshTokenRepository.findByToken(hashToken(tokenStr))
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh token"));

        String tokenEmail = jwtUtils.extractEmail(tokenStr);
        if (tokenEmail == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh token signature");
        }
        if (!tokenEmail.equals(storedToken.getUser().getEmail())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token mismatch");
        }

        if (storedToken.isRevoked() || storedToken.getExpiryDate().isBefore(LocalDateTime.now(clock))) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token revoked or expired");
        }
        
        return storedToken;
    }

    public void save(RefreshToken token) {
        refreshTokenRepository.save(token);
    }
}
