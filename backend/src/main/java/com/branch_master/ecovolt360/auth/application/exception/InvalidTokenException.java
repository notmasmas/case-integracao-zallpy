package com.branch_master.ecovolt360.auth.application.exception;

public class InvalidTokenException extends RuntimeException {

    public InvalidTokenException() {
        super("Sessão inválida ou expirada. Faça login novamente.");
    }
}
