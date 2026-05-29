package com.macrotrack.auth.security;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.macrotrack.shared.dto.ErrorResponse;
import java.time.Instant;

@Slf4j
@Component
public class RateLimitingFilter extends OncePerRequestFilter {

    private final Cache<String, Bucket> cache;
    private final int rpm;
    private final ObjectMapper objectMapper;

    public RateLimitingFilter(@org.springframework.beans.factory.annotation.Value("${app.rate-limit.rpm:10}") int rpm, ObjectMapper objectMapper) {
        this.rpm = rpm;
        this.objectMapper = objectMapper;
        this.cache = Caffeine.newBuilder()
            .expireAfterAccess(Duration.ofHours(1))
            .maximumSize(100_000)
            .build();
    }

    private Bucket resolveBucket(String ip) {
        return cache.get(ip, this::newBucket);
    }

    private Bucket newBucket(String ip) {
        Bandwidth limit = Bandwidth.builder()
            .capacity(rpm)
            .refillIntervally(rpm, Duration.ofMinutes(1))
            .build();
        return Bucket.builder().addLimit(limit).build();
    }

    private String extractClientIp(HttpServletRequest request) {
        String ip = java.util.Optional.ofNullable(request.getHeader("X-Forwarded-For"))
            .map(h -> h.split(",")[0].trim())
            .or(() -> java.util.Optional.ofNullable(request.getHeader("X-Real-IP")))
            .orElse(request.getRemoteAddr());
        if (ip != null && ip.length() > 45) {
            ip = ip.substring(0, 45);
        }
        return ip;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();

        if (path.startsWith("/api/v1/")) {
            String ip = extractClientIp(request);

            Bucket bucket = resolveBucket(ip);

            if (!bucket.tryConsume(1)) {
                log.warn("Rate limit exceeded for IP: {} on path: {}", ip, path);
                
                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                response.setContentType("application/json; charset=UTF-8");
                response.getWriter().write("{\"status\":429,\"message\":\"Too many requests. Please try again later.\",\"timestamp\":\"" + java.time.Instant.now() + "\"}");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
