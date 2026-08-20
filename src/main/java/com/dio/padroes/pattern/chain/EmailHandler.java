package com.dio.padroes.pattern.chain;

import com.dio.padroes.domain.repository.ClienteRepository;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Order(20)
@Component
public class EmailHandler extends ValidacaoClienteHandler {

    private static final Pattern FORMATO = Pattern.compile("^[A-Za-z0-9_.+-]+@[A-Za-z0-9-]+([.][A-Za-z0-9-]+)+$");

    private final ClienteRepository clienteRepository;

    public EmailHandler(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    @Override
    protected void executar(ContextoValidacaoCliente contexto) {
        String email = contexto.getEmail() == null ? "" : contexto.getEmail().trim().toLowerCase();
        contexto.setEmail(email);

        if (email.isBlank()) {
            contexto.adicionarErro("email: é obrigatório");
            return;
        }
        if (!FORMATO.matcher(email).matches()) {
            contexto.adicionarErro("email: formato inválido (ex.: maria@empresa.com.br)");
            return;
        }

        boolean duplicado = contexto.getIdClienteEmEdicao() == null
                ? clienteRepository.existsByEmailIgnoreCase(email)
                : clienteRepository.existsByEmailIgnoreCaseAndIdNot(email, contexto.getIdClienteEmEdicao());

        if (duplicado) {
            contexto.adicionarErro("email: já existe um cliente cadastrado com " + email);
        }
    }

    @Override
    public String nome() {
        return "EmailHandler";
    }

    @Override
    public String descricao() {
        return "Valida o formato do e-mail e garante que ele não esteja em uso por outro cliente";
    }
}
