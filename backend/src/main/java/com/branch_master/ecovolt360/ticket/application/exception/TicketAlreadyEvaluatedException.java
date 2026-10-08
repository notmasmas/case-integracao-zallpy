package com.branch_master.ecovolt360.ticket.application.exception;

public class TicketAlreadyEvaluatedException extends RuntimeException {

    public TicketAlreadyEvaluatedException() {
        super("Este chamado já foi avaliado.");
    }
}
