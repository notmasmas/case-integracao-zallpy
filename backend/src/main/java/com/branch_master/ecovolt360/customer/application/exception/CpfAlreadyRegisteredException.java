package com.branch_master.ecovolt360.customer.application.exception;

public class CpfAlreadyRegisteredException extends RuntimeException {

    public CpfAlreadyRegisteredException() {
        super("Este CPF já possui cadastro.");
    }
}
