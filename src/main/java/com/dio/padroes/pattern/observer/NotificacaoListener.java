package com.dio.padroes.pattern.observer;

import com.dio.padroes.pattern.template.EmailNotificacao;
import com.dio.padroes.pattern.template.SmsNotificacao;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Order(2)
@Component
public class NotificacaoListener {

    private final EmailNotificacao email;
    private final SmsNotificacao sms;

    public NotificacaoListener(EmailNotificacao email, SmsNotificacao sms) {
        this.email = email;
        this.sms = sms;
    }

    // sincrono por enquanto; se pesar, e so anotar com @Async
    @EventListener
    public void aoCriarCliente(ClienteCriadoEvent evento) {
        email.enviar(evento.cliente(), "Bem-vindo(a)!",
                "seu cadastro foi criado com sucesso e já podemos entregar no seu endereço.");
        sms.enviar(evento.cliente(), "Bem-vindo(a)!", "cadastro criado com sucesso!");
    }

    @EventListener
    public void aoAtualizarCliente(ClienteAtualizadoEvent evento) {
        email.enviar(evento.cliente(), "Cadastro atualizado",
                "confirmamos a atualização dos seus dados. Se não foi você, entre em contato conosco.");
    }
}
