package com.branch_master.ecovolt360.ticket.application.exception;


public class TicketNotEvaluatedException extends RuntimeException {

    public TicketNotEvaluatedException() {
        super("Este chamado não pode ser avaliado.");
    }
}