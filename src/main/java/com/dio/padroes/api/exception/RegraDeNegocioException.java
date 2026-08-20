package com.dio.padroes.api.exception;

import java.util.List;

public class RegraDeNegocioException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final List<String> erros;

    public RegraDeNegocioException(String mensagem) {
        this(mensagem, List.of(mensagem));
    }

    public RegraDeNegocioException(String mensagem, List<String> erros) {
        super(mensagem);
        this.erros = List.copyOf(erros);
    }

    public List<String> getErros() {
        return erros;
    }
}
