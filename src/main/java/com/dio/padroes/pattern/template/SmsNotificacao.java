package com.dio.padroes.pattern.template;

import com.dio.padroes.domain.model.Cliente;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class SmsNotificacao extends NotificacaoTemplate {

    private static final Logger log = LoggerFactory.getLogger(SmsNotificacao.class);
    // SMS corta o corpo; e o unico passo que muda em relacao ao e-mail
    private static final int LIMITE_CARACTERES = 140;

    public SmsNotificacao(CaixaDeSaida caixaDeSaida) {
        super(caixaDeSaida);
    }

    @Override
    public String canal() {
        return "SMS";
    }

    @Override
    protected String extrairDestinatario(Cliente cliente) {
        return cliente.getTelefone();
    }

    @Override
    protected String montarCorpo(Cliente cliente, String mensagem) {
        String corpo = "%s: %s".formatted(primeiroNome(cliente), mensagem);
        return corpo.length() <= LIMITE_CARACTERES ? corpo : corpo.substring(0, LIMITE_CARACTERES - 3) + "...";
    }

    @Override
    protected void despachar(String destinatario, String assunto, String corpo) {
        log.info("[SMS] para {} | {}", destinatario, corpo);
    }
}
