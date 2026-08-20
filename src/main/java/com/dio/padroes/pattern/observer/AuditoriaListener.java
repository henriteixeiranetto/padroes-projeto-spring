package com.dio.padroes.pattern.observer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Order(1)
@Component
public class AuditoriaListener {

    private static final Logger log = LoggerFactory.getLogger(AuditoriaListener.class);

    private final LinhaDoTempo linhaDoTempo;

    public AuditoriaListener(LinhaDoTempo linhaDoTempo) {
        this.linhaDoTempo = linhaDoTempo;
    }

    @EventListener
    public void aoOcorrerEvento(EventoDeCliente evento) {
        log.info("[AUDITORIA] {} - {}", evento.tipo(), evento.resumo());
        linhaDoTempo.registrar(evento, "AuditoriaListener");
    }
}
