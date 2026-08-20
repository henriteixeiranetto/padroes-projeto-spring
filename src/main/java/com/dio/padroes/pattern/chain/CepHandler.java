package com.dio.padroes.pattern.chain;

import com.dio.padroes.support.Ceps;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Order(40)
@Component
public class CepHandler extends ValidacaoClienteHandler {

    @Override
    protected void executar(ContextoValidacaoCliente contexto) {
        String cep = contexto.getCep();
        if (cep == null || cep.isBlank()) {
            contexto.adicionarErro("cep: é obrigatório");
            return;
        }
        if (!Ceps.valido(cep)) {
            contexto.adicionarErro("cep: precisa ter 8 dígitos (ex.: 01001-000)");
            return;
        }
        contexto.setCep(Ceps.formatar(cep));
    }

    @Override
    public String nome() {
        return "CepHandler";
    }

    @Override
    public String descricao() {
        return "Exige 8 dígitos e normaliza o CEP para 00000-000 antes da consulta ao ViaCEP";
    }
}
