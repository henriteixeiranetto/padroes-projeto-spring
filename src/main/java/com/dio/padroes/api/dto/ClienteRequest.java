package com.dio.padroes.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// bean validation cuida do formato da requisicao (400); as regras de negocio ficam na cadeia (422)
@Schema(name = "ClienteRequest", description = "Dados para cadastrar ou atualizar um cliente")
public record ClienteRequest(

        @Schema(example = "Maria Silva")
        @NotBlank(message = "nome: é obrigatório")
        @Size(max = 120, message = "nome: no máximo 120 caracteres")
        String nome,

        @Schema(example = "maria.silva@empresa.com.br")
        @NotBlank(message = "email: é obrigatório")
        @Size(max = 120, message = "email: no máximo 120 caracteres")
        String email,

        @Schema(example = "(11) 98765-4321", description = "Opcional")
        @Size(max = 20, message = "telefone: no máximo 20 caracteres")
        String telefone,

        @Schema(example = "01001-000")
        @NotBlank(message = "cep: é obrigatório")
        @Size(max = 9, message = "cep: no máximo 9 caracteres")
        String cep) {
}
