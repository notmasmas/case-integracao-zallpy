package com.branch_master.ecovolt360.messages.presentation.exception;

import com.branch_master.ecovolt360.auth.presentation.dto.ApiError;
import com.branch_master.ecovolt360.messages.presentation.controller.MessageController;
import com.branch_master.ecovolt360.ticket.application.exception.TicketClosedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice(assignableTypes = MessageController.class)
public class MessageExceptionHandler {

    @ExceptionHandler(TicketClosedException.class)
    public ResponseEntity<ApiError> handleTicketClosed(TicketClosedException exception) {
        ApiError error = new ApiError("TICKET_CLOSED", exception.getMessage(), Map.of());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }
}
