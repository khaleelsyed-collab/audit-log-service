package com.example.audit.exception;

import java.time.Instant;

/**
 * Standard payload returned by the global REST exception handler.
 */
public record ApiErrorResponse(
        Instant timestamp,
        int status,
        String error,
        String message,
        String path
) {
}
