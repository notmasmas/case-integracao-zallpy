package com.branch_master.ecovolt360.ticket.presentation.exception;

import com.branch_master.ecovolt360.auth.presentation.dto.ApiError;
import com.branch_master.ecovolt360.ticket.application.exception.TicketAlreadyAssignedException;
import com.branch_master.ecovolt360.ticket.application.exception.TicketAlreadyClosedException;
import com.branch_master.ecovolt360.ticket.application.exception.TicketAlreadyEvaluatedException;
import com.branch_master.ecovolt360.ticket.application.exception.TicketClosedException;
import com.branch_master.ecovolt360.ticket.application.exception.TicketNotAssignedToSupportException;
import com.branch_master.ecovolt360.ticket.application.exception.TicketNotEvaluatedException;
import com.branch_master.ecovolt360.ticket.application.exception.TicketNotFoundException;
import com.branch_master.ecovolt360.ticket.presentation.controller.CustomerTicketController;
import com.branch_master.ecovolt360.ticket.presentation.controller.SupportTicketController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice(assignableTypes = {CustomerTicketController.class, SupportTicketController.class})
public class TicketExceptionHandler {

    @ExceptionHandler(TicketNotFoundException.class)
    public ResponseEntity<ApiError> handleTicketNotFound(TicketNotFoundException exception) {
        ApiError error = new ApiError("TICKET_NOT_FOUND", exception.getMessage(), Map.of());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(TicketAlreadyClosedException.class)
    public ResponseEntity<ApiError> handleTicketAlreadyClosed(TicketAlreadyClosedException exception) {
        ApiError error = new ApiError("TICKET_ALREADY_CLOSED", exception.getMessage(), Map.of());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(TicketNotEvaluatedException.class)
    public ResponseEntity<ApiError> handleTicketNotEvaluated(TicketNotEvaluatedException exception) {
        ApiError error = new ApiError("TICKET_NOT_EVALUATED", exception.getMessage(), Map.of());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(TicketAlreadyEvaluatedException.class)
    public ResponseEntity<ApiError> handleTicketAlreadyEvaluated(TicketAlreadyEvaluatedException exception) {
        ApiError error = new ApiError("TICKET_ALREADY_EVALUATED", exception.getMessage(), Map.of());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(TicketAlreadyAssignedException.class)
    public ResponseEntity<ApiError> handleTicketAlreadyAssigned(TicketAlreadyAssignedException exception) {
        ApiError error = new ApiError("TICKET_ALREADY_ASSIGNED", exception.getMessage(), Map.of());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(TicketNotAssignedToSupportException.class)
    public ResponseEntity<ApiError> handleTicketNotAssignedToSupport(TicketNotAssignedToSupportException exception) {
        ApiError error = new ApiError("TICKET_NOT_ASSIGNED", exception.getMessage(), Map.of());

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    @ExceptionHandler(TicketClosedException.class)
    public ResponseEntity<ApiError> handleTicketClosed(TicketClosedException exception) {
        ApiError error = new ApiError("TICKET_CLOSED", exception.getMessage(), Map.of());

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

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleUnreadableBody(HttpMessageNotReadableException exception) {
        ApiError error = new ApiError("INVALID_REQUEST_BODY", "Envie um corpo JSON válido com os campos esperados.", Map.of());

        return ResponseEntity.badRequest().body(error);
    }
}
