package com.branch_master.ecovolt360.auth.presentation.exception;

import com.branch_master.ecovolt360.auth.application.exception.ForbiddenException;
import com.branch_master.ecovolt360.auth.application.exception.InvalidTokenException;
import com.branch_master.ecovolt360.auth.presentation.dto.ApiError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class AccessExceptionHandler {

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ApiError> handleInvalidToken(InvalidTokenException exception) {
        ApiError error = new ApiError("UNAUTHORIZED", exception.getMessage(), Map.of());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ApiError> handleForbidden(ForbiddenException exception) {
        ApiError error = new ApiError("FORBIDDEN", exception.getMessage(), Map.of());

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }
}
