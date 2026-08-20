package com.dio.padroes.pattern.chain;

public abstract class ValidacaoClienteHandler {

    private ValidacaoClienteHandler proximo;

    public ValidacaoClienteHandler encadearCom(ValidacaoClienteHandler proximo) {
        this.proximo = proximo;
        return proximo;
    }

    // final para garantir que ninguem esqueca de repassar para o proximo elo
    public final void validar(ContextoValidacaoCliente contexto) {
        contexto.registrarPasso(nome());
        executar(contexto);
        if (proximo != null) {
            proximo.validar(contexto);
        }
    }

    protected abstract void executar(ContextoValidacaoCliente contexto);

    public abstract String nome();

    public abstract String descricao();
}
