package com.branch_master.ecovolt360.faq.application.exception;

public class FaqUnavailableException extends RuntimeException {

    public static final String MESSAGE =
            "Não foi possível carregar as perguntas frequentes. Tente novamente mais tarde.";

    public FaqUnavailableException(Throwable cause) {
        super(MESSAGE, cause);
    }
}
