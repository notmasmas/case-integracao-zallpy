package com.branch_master.ecovolt360.ticket.application.exception;

public class TicketNotAssignedToSupportException extends RuntimeException{
    
    public TicketNotAssignedToSupportException(){
        super ("Suporte não pode responder ao chamado");
    }
}
