package com.branch_master.ecovolt360.customer.application.exception;

public class CpfNotFoundException extends RuntimeException {

    public CpfNotFoundException() {
        super("CPF não cadastrado.");
    }
}
