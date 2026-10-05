package com.branch_master.ecovolt360.ticket.presentation.exception;

import com.branch_master.ecovolt360.auth.presentation.dto.ApiError;
import com.branch_master.ecovolt360.ticket.application.exception.TicketNotFoundException;
import com.branch_master.ecovolt360.ticket.presentation.controller.TicketController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice(assignableTypes = TicketController.class)
public class TicketExceptionHandler {

    @ExceptionHandler(TicketNotFoundException.class)
    public ResponseEntity<ApiError> handleTicketNotFound(TicketNotFoundException exception) {
        ApiError error = new ApiError("TICKET_NOT_FOUND", exception.getMessage(), Map.of());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
}
