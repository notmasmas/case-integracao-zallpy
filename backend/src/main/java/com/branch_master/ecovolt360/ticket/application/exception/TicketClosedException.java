package com.branch_master.ecovolt360.ticket.application.exception;

public class TicketClosedException extends RuntimeException {

    public TicketClosedException() {
        super("Este chamado não aceita novas mensagens.");
    }
}
