package com.branch_master.ecovolt360.faq.presentation.exception;

import com.branch_master.ecovolt360.auth.presentation.dto.ApiError;
import com.branch_master.ecovolt360.faq.application.exception.FaqUnavailableException;
import com.branch_master.ecovolt360.faq.presentation.controller.FaqController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice(assignableTypes = FaqController.class)
public class FaqExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(FaqExceptionHandler.class);

    @ExceptionHandler(FaqUnavailableException.class)
    public ResponseEntity<ApiError> handleFaqUnavailable(FaqUnavailableException exception) {
        log.error("Falha ao carregar FAQs", exception);

        ApiError error = new ApiError("FAQ_UNAVAILABLE", exception.getMessage(), Map.of());

        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(error);
    }
}
