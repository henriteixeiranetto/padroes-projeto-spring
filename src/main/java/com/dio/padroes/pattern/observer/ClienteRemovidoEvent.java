package com.dio.padroes.pattern.observer;

import java.time.LocalDateTime;

public record ClienteRemovidoEvent(Long id, String nome, String email, LocalDateTime ocorridoEm)
        implements EventoDeCliente {

    public ClienteRemovidoEvent(Long id, String nome, String email) {
        this(id, nome, email, LocalDateTime.now());
    }

    @Override
    public String tipo() {
        return "CLIENTE_REMOVIDO";
    }

    @Override
    public String resumo() {
        return "Cliente %s (id %d) removido".formatted(nome, id);
    }
}
