package com.dio.padroes.pattern.observer;

import java.time.LocalDateTime;

// sealed so para deixar explicito o catalogo de eventos do dominio
public sealed interface EventoDeCliente
        permits ClienteCriadoEvent, ClienteAtualizadoEvent, ClienteRemovidoEvent {

    String tipo();

    String resumo();

    LocalDateTime ocorridoEm();
}
