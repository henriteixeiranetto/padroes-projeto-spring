package com.dio.padroes.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "ModalidadeResponse", description = "Modalidade de frete suportada e a estratégia que a implementa")
public record ModalidadeResponse(String modalidade, String rotulo, String descricao, String estrategia) {
}
