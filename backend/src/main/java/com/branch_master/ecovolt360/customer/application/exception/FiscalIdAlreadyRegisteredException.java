package com.branch_master.ecovolt360.customer.application.exception;

public class FiscalIdAlreadyRegisteredException extends RuntimeException {

    public FiscalIdAlreadyRegisteredException() {
        super("Este CPF/CNPJ já possui cadastro.");
    }
}
