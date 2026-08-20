package com.dio.padroes.pattern.adapter;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

// o campo erro as vezes vem como booleano, as vezes como a string "true"
@JsonIgnoreProperties(ignoreUnknown = true)
public record ViaCepResposta(
        String cep,
        String logradouro,
        String complemento,
        String bairro,
        String localidade,
        String uf,
        String ibge,
        String ddd,
        String erro) {

    public boolean cepInexistente() {
        return "true".equalsIgnoreCase(erro);
    }
}
