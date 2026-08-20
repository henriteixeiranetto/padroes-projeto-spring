package com.dio.padroes.api.exception;

public class IntegracaoIndisponivelException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public IntegracaoIndisponivelException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
