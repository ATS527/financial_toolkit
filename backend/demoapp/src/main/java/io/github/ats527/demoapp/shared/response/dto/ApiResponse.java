package io.github.ats527.demoapp.shared.response.dto;

import java.time.OffsetDateTime;

import org.springframework.http.HttpStatus;

public record ApiResponse<T>(int status, String message, T data, OffsetDateTime timestamp) {
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<T>(HttpStatus.OK.value(), message, data, OffsetDateTime.now());
    }

    public static <T> ApiResponse<T> created(String message, T data) {
        return new ApiResponse<T>(HttpStatus.CREATED.value(), message, data, OffsetDateTime.now());
    }
}
