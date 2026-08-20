package com.dio.padroes.service;

import com.dio.padroes.api.exception.IntegracaoIndisponivelException;
import com.dio.padroes.api.exception.RecursoNaoEncontradoException;
import com.dio.padroes.domain.model.Endereco;
import com.dio.padroes.pattern.adapter.ViaCepAdapter;
import com.dio.padroes.pattern.adapter.ViaCepClient;
import com.dio.padroes.pattern.adapter.ViaCepResposta;
import com.dio.padroes.support.Ceps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

@Service("viaCepService")
public class ViaCepService implements CepService {

    private static final Logger log = LoggerFactory.getLogger(ViaCepService.class);

    private final ViaCepClient client;
    private final ViaCepAdapter adapter;

    public ViaCepService(ViaCepClient client, ViaCepAdapter adapter) {
        this.client = client;
        this.adapter = adapter;
    }

    @Override
    public Endereco buscar(String cep) {
        String digitos = Ceps.somenteDigitos(cep);
        try {
            log.info("Consultando ViaCEP para o CEP {}", Ceps.formatar(digitos));
            ViaCepResposta resposta = client.consultar(digitos);
            if (resposta == null || resposta.cepInexistente()) {
                throw new RecursoNaoEncontradoException("CEP %s não encontrado no ViaCEP".formatted(Ceps.formatar(digitos)));
            }
            return adapter.adaptar(resposta, digitos);
        } catch (RestClientException e) {
            throw new IntegracaoIndisponivelException("Não foi possível consultar o ViaCEP: " + e.getMessage(), e);
        }
    }

    @Override
    public String identificacao() {
        return "ViaCepService (integração HTTP real)";
    }
}
