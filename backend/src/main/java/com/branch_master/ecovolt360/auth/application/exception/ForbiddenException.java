package com.branch_master.ecovolt360.auth.application.exception;

public class ForbiddenException extends RuntimeException {

    public ForbiddenException() {
        super("Você não tem permissão para acessar este recurso.");
    }
}
