package com.branch_master.ecovolt360.ticket.application.exception;

public class TicketAlreadyClosedException extends RuntimeException {

    public TicketAlreadyClosedException() {
        super("Chamado encerrado não pode ter o status alterado.");
    }
}
