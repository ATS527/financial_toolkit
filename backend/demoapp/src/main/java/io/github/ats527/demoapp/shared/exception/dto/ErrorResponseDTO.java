package io.github.ats527.demoapp.shared.exception.dto;

public record ErrorResponseDTO(int status, String message, long timestamp) {
}
