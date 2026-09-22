package com.branch_master.ecovolt360.auth.presentation.exception;

import com.branch_master.ecovolt360.auth.application.exception.InvalidCredentialsException;
import com.branch_master.ecovolt360.auth.presentation.controller.LoginController;
import com.branch_master.ecovolt360.auth.presentation.dto.ApiError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice(assignableTypes = LoginController.class)
public class AuthExceptionHandler {

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ApiError> handlerInvalidCredentials(
            InvalidCredentialsException exception
    ) {
        ApiError error = new ApiError(
                "INVALID_CREDENTIALS",
                exception.getMessage(),
                Map.of()
        );

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(
            MethodArgumentNotValidException exception
    ) {
        Map<String, String> fieldErrors = new LinkedHashMap<>();

        exception.getBindingResult().getFieldErrors().forEach(error ->
                fieldErrors.putIfAbsent(
                        error.getField(),
                        error.getDefaultMessage()
                )
        );

        ApiError error = new ApiError(
                "VALIDATION_ERROR",
                "Verifique os campos informados.",
                fieldErrors
        );

        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleUnreadableBody(
            HttpMessageNotReadableException exception
    ) {
        ApiError error = new ApiError(
                "INVALID_REQUEST_BODY",
                "Envie um corpo JSON válido com e-mail e senha.",
                Map.of()
        );

        return ResponseEntity.badRequest().body(error);
    }
}