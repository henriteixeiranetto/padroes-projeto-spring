package com.dio.padroes.pattern.observer;

import com.dio.padroes.domain.model.Cliente;

import java.time.LocalDateTime;

public record ClienteCriadoEvent(Cliente cliente, LocalDateTime ocorridoEm) implements EventoDeCliente {

    public ClienteCriadoEvent(Cliente cliente) {
        this(cliente, LocalDateTime.now());
    }

    @Override
    public String tipo() {
        return "CLIENTE_CRIADO";
    }

    @Override
    public String resumo() {
        String cidade = cliente.getEndereco() == null ? "-" : cliente.getEndereco().getLocalidade();
        return "Cliente %s cadastrado (%s)".formatted(cliente.getNome(), cidade);
    }
}
