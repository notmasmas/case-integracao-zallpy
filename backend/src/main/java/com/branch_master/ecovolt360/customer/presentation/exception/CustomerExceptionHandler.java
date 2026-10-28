package com.branch_master.ecovolt360.customer.presentation.exception;

import com.branch_master.ecovolt360.auth.presentation.dto.ApiError;
import com.branch_master.ecovolt360.customer.application.exception.CpfAlreadyRegisteredException;
import com.branch_master.ecovolt360.customer.application.exception.CpfNotFoundException;
import com.branch_master.ecovolt360.customer.application.exception.EmailAlreadyInUseException;
import com.branch_master.ecovolt360.customer.controller.CustomerController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice(assignableTypes = CustomerController.class)
public class CustomerExceptionHandler {

    @ExceptionHandler(CpfNotFoundException.class)
    public ResponseEntity<ApiError> handleCpfNotFound(CpfNotFoundException exception) {
        ApiError error = new ApiError("CPF_NOT_FOUND", exception.getMessage(), Map.of());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(CpfAlreadyRegisteredException.class)
    public ResponseEntity<ApiError> handleCpfAlreadyRegistered(CpfAlreadyRegisteredException exception) {
        ApiError error = new ApiError("CPF_ALREADY_REGISTERED", exception.getMessage(), Map.of());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(EmailAlreadyInUseException.class)
    public ResponseEntity<ApiError> handleEmailAlreadyInUse(EmailAlreadyInUseException exception) {
        ApiError error = new ApiError("EMAIL_ALREADY_IN_USE", exception.getMessage(), Map.of());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException exception) {
        Map<String, String> fieldErrors = new LinkedHashMap<>();

        exception.getBindingResult().getFieldErrors().forEach(error ->
                fieldErrors.putIfAbsent(error.getField(), error.getDefaultMessage())
        );

        ApiError error = new ApiError("VALIDATION_ERROR", "Verifique os campos informados.", fieldErrors);

        return ResponseEntity.badRequest().body(error);
    }
}
