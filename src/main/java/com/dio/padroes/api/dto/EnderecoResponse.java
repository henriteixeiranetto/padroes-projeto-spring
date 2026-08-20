package com.dio.padroes.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "EnderecoResponse", description = "Endereço resolvido a partir do CEP")
public record EnderecoResponse(
        String cep,
        String logradouro,
        String complemento,
        String bairro,
        String localidade,
        String uf,
        String ddd,
        @Schema(description = "De onde veio o dado: VIACEP, CACHE ou OFFLINE", example = "VIACEP")
        String fonte) {
}
