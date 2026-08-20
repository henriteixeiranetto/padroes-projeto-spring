package com.dio.padroes.pattern.adapter;

import com.dio.padroes.domain.model.Endereco;
import com.dio.padroes.support.Ceps;
import org.springframework.stereotype.Component;

// unico ponto do projeto que conhece o formato do ViaCEP
@Component
public class ViaCepAdapter {

    public static final String FONTE = "VIACEP";

    public Endereco adaptar(ViaCepResposta resposta, String cepConsultado) {
        return Endereco.builder()
                .cep(Ceps.formatar(resposta.cep() != null ? resposta.cep() : cepConsultado))
                .logradouro(vazioParaNulo(resposta.logradouro()))
                .complemento(vazioParaNulo(resposta.complemento()))
                .bairro(vazioParaNulo(resposta.bairro()))
                .localidade(vazioParaNulo(resposta.localidade()))
                .uf(vazioParaNulo(resposta.uf()))
                .ibge(vazioParaNulo(resposta.ibge()))
                .ddd(vazioParaNulo(resposta.ddd()))
                .fonte(FONTE)
                .build();
    }

    private String vazioParaNulo(String valor) {
        return valor == null || valor.isBlank() ? null : valor.trim();
    }
}
