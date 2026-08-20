package com.dio.padroes.pattern.observer;

import com.dio.padroes.domain.model.Cliente;

import java.time.LocalDateTime;

public record ClienteAtualizadoEvent(Cliente cliente, LocalDateTime ocorridoEm) implements EventoDeCliente {

    public ClienteAtualizadoEvent(Cliente cliente) {
        this(cliente, LocalDateTime.now());
    }

    @Override
    public String tipo() {
        return "CLIENTE_ATUALIZADO";
    }

    @Override
    public String resumo() {
        return "Cadastro de %s atualizado".formatted(cliente.getNome());
    }
}
