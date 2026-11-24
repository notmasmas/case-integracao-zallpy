package com.branch_master.ecovolt360.customer.application.exception;

public class FiscalIdNotFoundException extends RuntimeException {

    public FiscalIdNotFoundException() {
        super("CPF/CNPJ não cadastrado.");
    }
}
