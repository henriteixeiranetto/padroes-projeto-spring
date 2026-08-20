package com.dio.padroes.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(name = "PadraoResponse", description = "Descrição didática de um padrão aplicado no projeto")
public record PadraoResponse(
        String padrao,
        String categoria,
        String problema,
        String solucao,
        List<String> classes,
        String comoVerFuncionando) {
}
