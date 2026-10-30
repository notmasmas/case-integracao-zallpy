package com.branch_master.ecovolt360.ticket.application.exception;

public class TicketAlreadyAssignedException extends RuntimeException{ 

    public TicketAlreadyAssignedException (){
        super("Esse chamado está atribuído por outro colaborador");
    }
    
}
