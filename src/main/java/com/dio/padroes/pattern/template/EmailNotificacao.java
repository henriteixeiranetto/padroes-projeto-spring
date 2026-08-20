package com.dio.padroes.pattern.template;

import com.dio.padroes.domain.model.Cliente;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class EmailNotificacao extends NotificacaoTemplate {

    private static final Logger log = LoggerFactory.getLogger(EmailNotificacao.class);

    public EmailNotificacao(CaixaDeSaida caixaDeSaida) {
        super(caixaDeSaida);
    }

    @Override
    public String canal() {
        return "EMAIL";
    }

    @Override
    protected String extrairDestinatario(Cliente cliente) {
        return cliente.getEmail();
    }

    @Override
    protected String montarCorpo(Cliente cliente, String mensagem) {
        return super.montarCorpo(cliente, mensagem)
                + System.lineSeparator()
                + System.lineSeparator()
                + "Equipe Padrões de Projeto - DIO";
    }

    @Override
    protected void despachar(String destinatario, String assunto, String corpo) {
        log.info("[EMAIL] para {} | assunto: {}", destinatario, assunto);
    }
}
