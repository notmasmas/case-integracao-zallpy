package com.branch_master.ecovolt360.auth.application.exception;

/**
 * Sessão encerrada por inatividade (limite de 15 minutos). Distinta de um token inválido
 * genérico para que o cliente da API saiba o motivo da rejeição.
 */
public class SessionExpiredException extends InvalidTokenException {

    public static final String CODE = "SESSION_EXPIRED";

    public SessionExpiredException() {
        super("Sua sessão expirou por inatividade. Faça login novamente.");
    }
}
