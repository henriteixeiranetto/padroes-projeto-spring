package com.dio.padroes.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class StartupLogger {

    private static final Logger log = LoggerFactory.getLogger(StartupLogger.class);

    @Value("${server.port:8080}")
    private int porta;

    @EventListener(ApplicationReadyEvent.class)
    public void aoIniciar() {
        String base = "http://localhost:" + porta;
        log.info("""
                
                =====================================================================
                  Padroes de Projeto na Pratica - aplicacao no ar
                ---------------------------------------------------------------------
                  Interface web ....... {}
                  Swagger UI .......... {}/swagger-ui.html
                  Catalogo de padroes . {}/api/padroes
                  Console do H2 ....... {}/h2-console  (JDBC: jdbc:h2:mem:padroesdb, user: sa)
                  Health .............. {}/actuator/health
                =====================================================================
                """, base, base, base, base, base);
    }
}
