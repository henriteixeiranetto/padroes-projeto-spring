package com.dio.padroes.pattern.chain;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Order(30)
@Component
public class TelefoneHandler extends ValidacaoClienteHandler {

    @Override
    protected void executar(ContextoValidacaoCliente contexto) {
        String informado = contexto.getTelefone();
        if (informado == null || informado.isBlank()) {
            contexto.setTelefone(null);
            return;
        }

        String digitos = informado.replaceAll("[^0-9]", "");
        if (digitos.length() != 10 && digitos.length() != 11) {
            contexto.adicionarErro("telefone: informe DDD + número (10 ou 11 dígitos)");
            return;
        }

        String ddd = digitos.substring(0, 2);
        String numero = digitos.substring(2);
        String prefixo = numero.substring(0, numero.length() - 4);
        String sufixo = numero.substring(numero.length() - 4);
        contexto.setTelefone("(%s) %s-%s".formatted(ddd, prefixo, sufixo));
    }

    @Override
    public String nome() {
        return "TelefoneHandler";
    }

    @Override
    public String descricao() {
        return "Campo opcional: quando preenchido, valida DDD + número e aplica a máscara (11) 91234-5678";
    }
}
