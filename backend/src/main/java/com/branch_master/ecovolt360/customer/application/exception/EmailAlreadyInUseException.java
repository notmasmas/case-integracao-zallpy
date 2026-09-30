package com.branch_master.ecovolt360.customer.application.exception;

public class EmailAlreadyInUseException extends RuntimeException {

    public EmailAlreadyInUseException() {
        super("Este e-mail já está em uso.");
    }
}
