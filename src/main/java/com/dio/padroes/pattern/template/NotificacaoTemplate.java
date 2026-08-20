package com.dio.padroes.pattern.template;

import com.dio.padroes.domain.model.Cliente;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.Optional;

public abstract class NotificacaoTemplate {

    private static final Logger log = LoggerFactory.getLogger(NotificacaoTemplate.class);

    private final CaixaDeSaida caixaDeSaida;

    protected NotificacaoTemplate(CaixaDeSaida caixaDeSaida) {
        this.caixaDeSaida = caixaDeSaida;
    }

    // vazio quando o cliente nao tem como ser contatado por esse canal (ex.: SMS sem telefone)
    public final Optional<Notificacao> enviar(Cliente cliente, String assunto, String mensagem) {
        String destinatario = extrairDestinatario(cliente);
        if (destinatario == null || destinatario.isBlank()) {
            log.debug("Cliente {} nao possui destino para o canal {}", cliente.getId(), canal());
            return Optional.empty();
        }

        String corpo = montarCorpo(cliente, mensagem);
        despachar(destinatario, assunto, corpo);

        Notificacao notificacao = new Notificacao(canal(), destinatario, assunto, corpo, LocalDateTime.now());
        caixaDeSaida.registrar(notificacao);
        return Optional.of(notificacao);
    }

    public abstract String canal();

    protected abstract String extrairDestinatario(Cliente cliente);

    protected abstract void despachar(String destinatario, String assunto, String corpo);

    protected String montarCorpo(Cliente cliente, String mensagem) {
        return "Olá, %s! %s".formatted(primeiroNome(cliente), mensagem);
    }

    protected final String primeiroNome(Cliente cliente) {
        String nome = cliente.getNome() == null ? "" : cliente.getNome().trim();
        int espaco = nome.indexOf(' ');
        return espaco > 0 ? nome.substring(0, espaco) : nome;
    }
}
