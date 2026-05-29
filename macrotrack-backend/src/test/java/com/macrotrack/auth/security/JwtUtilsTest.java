package com.macrotrack.auth.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilsTest {

    private JwtUtils jwtUtils;

    private static final String SECRET = "A_VERY_LONG_SECRET_KEY_THAT_MUST_BE_AT_LEAST_32_BYTES";
    private static final long EXPIRATION_MS = 3600000; // 1 hour
    private static final String EMAIL = "test@example.com";

    @BeforeEach
    void setUp() {
        jwtUtils = new JwtUtils();
        ReflectionTestUtils.setField(jwtUtils, "jwtSecret", SECRET);
        ReflectionTestUtils.setField(jwtUtils, "jwtExpirationMs", EXPIRATION_MS);
        ReflectionTestUtils.setField(jwtUtils, "jwtRefreshExpirationMs", EXPIRATION_MS * 24);
        jwtUtils.init();
    }

    @Test
    void init_throwsExceptionIfSecretTooShort() {
        JwtUtils shortSecretUtils = new JwtUtils();
        ReflectionTestUtils.setField(shortSecretUtils, "jwtSecret", "short");
        
        assertThrows(IllegalArgumentException.class, shortSecretUtils::init);
    }

    @Test
    void generateAccessToken_createsValidToken() {
        String token = jwtUtils.generateAccessToken(EMAIL);
        assertNotNull(token);
        assertFalse(token.isEmpty());
        
        String extractedEmail = jwtUtils.extractEmail(token);
        assertEquals(EMAIL, extractedEmail);
    }

    @Test
    void isTokenValid_returnsTrueForValidTokenAndMatchingUser() {
        String token = jwtUtils.generateAccessToken(EMAIL);
        UserDetails userDetails = new User(EMAIL, "password", Collections.emptyList());
        
        assertTrue(jwtUtils.isTokenValid(token, userDetails));
    }

    @Test
    void isTokenValid_returnsFalseForMismatchedUser() {
        String token = jwtUtils.generateAccessToken(EMAIL);
        UserDetails userDetails = new User("other@example.com", "password", Collections.emptyList());
        
        assertFalse(jwtUtils.isTokenValid(token, userDetails));
    }

    @Test
    void extractEmail_returnsNullForInvalidSignature() {
        String token = jwtUtils.generateAccessToken(EMAIL);
        // Tamper with the token
        String tamperedToken = token.substring(0, token.length() - 5) + "abcde";
        
        assertNull(jwtUtils.extractEmail(tamperedToken));
    }

    @Test
    void extractEmail_returnsNullForMalformedToken() {
        assertNull(jwtUtils.extractEmail("not.a.valid.token"));
    }

    @Test
    void isTokenValid_returnsFalseForExpiredToken() throws InterruptedException {
        ReflectionTestUtils.setField(jwtUtils, "jwtExpirationMs", 1L); // 1 ms expiration
        
        String token = jwtUtils.generateAccessToken(EMAIL);
        
        // Wait for token to expire
        Thread.sleep(10);
        
        UserDetails userDetails = new User(EMAIL, "password", Collections.emptyList());
        assertFalse(jwtUtils.isTokenValid(token, userDetails));
    }
}
