package com.dio.padroes.pattern.observer;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

@Component
public class LinhaDoTempo {

    private static final int LIMITE = 100;

    private final Deque<Registro> registros = new ArrayDeque<>();

    public synchronized void registrar(EventoDeCliente evento, String observador) {
        registros.addFirst(new Registro(evento.tipo(), evento.resumo(), observador, evento.ocorridoEm()));
        while (registros.size() > LIMITE) {
            registros.removeLast();
        }
    }

    public synchronized List<Registro> listar() {
        return List.copyOf(registros);
    }

    public synchronized void limpar() {
        registros.clear();
    }

    public record Registro(String tipo, String resumo, String observador, LocalDateTime ocorridoEm) {
    }
}
