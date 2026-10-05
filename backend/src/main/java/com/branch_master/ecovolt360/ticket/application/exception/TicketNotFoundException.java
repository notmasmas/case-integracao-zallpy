package com.branch_master.ecovolt360.ticket.application.exception;

public class TicketNotFoundException extends RuntimeException {

    public TicketNotFoundException() {
        super("Chamado não encontrado.");
    }
}
