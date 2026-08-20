package com.dio.padroes.pattern.template;

import org.springframework.stereotype.Component;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

@Component
public class CaixaDeSaida {

    private static final int LIMITE = 100;

    private final Deque<Notificacao> notificacoes = new ArrayDeque<>();

    public synchronized void registrar(Notificacao notificacao) {
        notificacoes.addFirst(notificacao);
        while (notificacoes.size() > LIMITE) {
            notificacoes.removeLast();
        }
    }

    public synchronized List<Notificacao> listar() {
        return List.copyOf(notificacoes);
    }

    public synchronized void limpar() {
        notificacoes.clear();
    }
}
