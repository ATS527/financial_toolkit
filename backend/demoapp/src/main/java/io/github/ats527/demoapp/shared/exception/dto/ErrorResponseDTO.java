package io.github.ats527.demoapp.shared.exception.dto;

import java.util.Map;

public record ErrorResponseDTO(int status, String message, Map<String, String> errors, long timestamp) {
}
