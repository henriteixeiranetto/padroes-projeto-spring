package com.dio.padroes.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(name = "ClienteResponse", description = "Cliente cadastrado, com endereço já resolvido")
public record ClienteResponse(
        Long id,
        String nome,
        String email,
        String telefone,
        EnderecoResponse endereco,
        LocalDateTime criadoEm,
        LocalDateTime atualizadoEm) {
}
