package io.github.ats527.demoapp.shared.exception.exceptions;

import org.springframework.http.HttpStatus;

public class BusinessLogicException extends BaseException {
    public BusinessLogicException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
