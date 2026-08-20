package com.dio.padroes.pattern.chain;

import com.dio.padroes.api.exception.RegraDeNegocioException;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

// roda a cadeia inteira e junta os erros; nao para no primeiro problema
@Component
public class CadeiaDeValidacaoCliente {

    private static final Logger log = LoggerFactory.getLogger(CadeiaDeValidacaoCliente.class);

    private final List<ValidacaoClienteHandler> elos;
    private ValidacaoClienteHandler primeiro;

    public CadeiaDeValidacaoCliente(List<ValidacaoClienteHandler> elos) {
        this.elos = List.copyOf(elos);
    }

    // a lista chega ordenada pelo @Order de cada elo
    @PostConstruct
    void montarCadeia() {
        if (elos.isEmpty()) {
            throw new IllegalStateException("Nenhum ValidacaoClienteHandler encontrado no contexto");
        }
        ValidacaoClienteHandler atual = elos.get(0);
        for (int i = 1; i < elos.size(); i++) {
            atual = atual.encadearCom(elos.get(i));
        }
        primeiro = elos.get(0);
        log.info("Cadeia de validacao montada: {}",
                elos.stream().map(ValidacaoClienteHandler::nome).collect(Collectors.joining(" -> ")));
    }

    public DadosClienteValidados validar(String nome, String email, String telefone, String cep, Long idClienteEmEdicao) {
        ContextoValidacaoCliente contexto = new ContextoValidacaoCliente(nome, email, telefone, cep, idClienteEmEdicao);
        primeiro.validar(contexto);

        if (contexto.temErros()) {
            throw new RegraDeNegocioException("Não foi possível salvar o cliente", contexto.getErros());
        }
        return new DadosClienteValidados(
                contexto.getNome(),
                contexto.getEmail(),
                contexto.getTelefone(),
                contexto.getCep(),
                contexto.getTrilha());
    }

    public List<ValidacaoClienteHandler> getElos() {
        return elos;
    }
}
