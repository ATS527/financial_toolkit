package io.github.ats527.demoapp.shared.exception;

import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import io.github.ats527.demoapp.shared.exception.dto.ErrorResponseDTO;
import io.github.ats527.demoapp.shared.exception.exceptions.BaseException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ErrorResponseDTO> handleBaseException(BaseException ex) {
        return buildResponse(ex.getStatus(), ex.getMessage(),null);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleGenericException(Exception ex) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred.", null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .collect(Collectors.toMap(
                error -> error.getField(),
                error -> error.getDefaultMessage()
            ));

        return buildResponse(HttpStatus.BAD_REQUEST, "Validation Error", errors);
    }

    private ResponseEntity<ErrorResponseDTO> buildResponse(HttpStatus status, String message, Map<String, String> errors) {
        ErrorResponseDTO errorResponse = new ErrorResponseDTO(
            status.value(),
            message,
            errors,
            System.currentTimeMillis()
        );

        return new ResponseEntity<ErrorResponseDTO>(errorResponse, status);
    }
}
