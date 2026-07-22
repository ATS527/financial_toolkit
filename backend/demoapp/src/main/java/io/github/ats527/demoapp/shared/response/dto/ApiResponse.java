package io.github.ats527.demoapp.shared.response.dto;

import java.time.OffsetDateTime;

public record ApiResponse<T>(String message, T data, OffsetDateTime timestamp) {
    public static <T> ApiResponse<T> of (String message, T data) {
        return new ApiResponse<T>(message, data, OffsetDateTime.now());
    }
}
