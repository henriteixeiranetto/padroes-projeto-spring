package com.dio.padroes.pattern.chain;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Order(10)
@Component
public class NomeHandler extends ValidacaoClienteHandler {

    @Override
    protected void executar(ContextoValidacaoCliente contexto) {
        String nome = contexto.getNome() == null ? "" : contexto.getNome().trim().replaceAll(" {2,}", " ");
        contexto.setNome(nome);

        if (nome.isBlank()) {
            contexto.adicionarErro("nome: é obrigatório");
            return;
        }
        if (nome.length() < 3) {
            contexto.adicionarErro("nome: precisa ter ao menos 3 caracteres");
        }
        if (nome.matches(".*[0-9].*")) {
            contexto.adicionarErro("nome: não pode conter números");
        }
    }

    @Override
    public String nome() {
        return "NomeHandler";
    }

    @Override
    public String descricao() {
        return "Normaliza espaços e exige um nome com 3+ caracteres, sem dígitos";
    }
}
