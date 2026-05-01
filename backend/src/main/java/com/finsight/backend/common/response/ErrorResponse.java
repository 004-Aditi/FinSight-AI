package com.finsight.backend.common.response;

import java.time.LocalDateTime;

public record ErrorResponse(
        boolean success,
        String message,
        String error,
        LocalDateTime timestamp) {
    public static ErrorResponse of(String message, String error) {
        return new ErrorResponse(
                false,
                message,
                error,
                LocalDateTime.now());
    }
}