package com.macrotrack.shared.dto;

import java.time.Instant;
import java.util.Map;

public record ErrorResponse(
    int status,
    String message,
    Instant timestamp,
    Map<String, String> fieldErrors
) {
    public ErrorResponse(int status, String message, Instant timestamp) {
        this(status, message, timestamp, null);
    }
}