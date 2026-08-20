package com.dio.padroes.pattern.facade;

import com.dio.padroes.api.exception.RecursoNaoEncontradoException;
import com.dio.padroes.domain.model.Cliente;
import com.dio.padroes.domain.model.Endereco;
import com.dio.padroes.domain.repository.ClienteRepository;
import com.dio.padroes.domain.repository.EnderecoRepository;
import com.dio.padroes.pattern.chain.CadeiaDeValidacaoCliente;
import com.dio.padroes.pattern.chain.DadosClienteValidados;
import com.dio.padroes.pattern.observer.ClienteAtualizadoEvent;
import com.dio.padroes.pattern.observer.ClienteCriadoEvent;
import com.dio.padroes.pattern.observer.ClienteRemovidoEvent;
import com.dio.padroes.service.CepService;
import com.dio.padroes.service.ClienteForm;
import com.dio.padroes.service.ClienteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ClienteFacade implements ClienteService {

    private static final Logger log = LoggerFactory.getLogger(ClienteFacade.class);

    private final ClienteRepository clienteRepository;
    private final EnderecoRepository enderecoRepository;
    private final CadeiaDeValidacaoCliente cadeiaDeValidacao;
    private final CepService cepService;
    private final ApplicationEventPublisher publisher;

    public ClienteFacade(ClienteRepository clienteRepository,
                         EnderecoRepository enderecoRepository,
                         CadeiaDeValidacaoCliente cadeiaDeValidacao,
                         CepService cepService,
                         ApplicationEventPublisher publisher) {
        this.clienteRepository = clienteRepository;
        this.enderecoRepository = enderecoRepository;
        this.cadeiaDeValidacao = cadeiaDeValidacao;
        this.cepService = cepService;
        this.publisher = publisher;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Cliente> listar(String filtroNome) {
        if (filtroNome == null || filtroNome.isBlank()) {
            return clienteRepository.findAllByOrderByNomeAsc();
        }
        return clienteRepository.findByNomeContainingIgnoreCaseOrderByNomeAsc(filtroNome.trim());
    }

    @Override
    @Transactional(readOnly = true)
    public Cliente buscarPorId(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cliente %d não encontrado".formatted(id)));
    }

    @Override
    @Transactional
    public Cliente criar(ClienteForm form) {
        DadosClienteValidados dados = cadeiaDeValidacao.validar(
                form.nome(), form.email(), form.telefone(), form.cep(), null);
        log.debug("Cadeia de validacao percorrida: {}", dados.trilha());

        Cliente cliente = Cliente.builder()
                .nome(dados.nome())
                .email(dados.email())
                .telefone(dados.telefone())
                .endereco(obterEndereco(dados.cep()))
                .build();

        Cliente salvo = clienteRepository.save(cliente);
        publisher.publishEvent(new ClienteCriadoEvent(salvo));
        return salvo;
    }

    @Override
    @Transactional
    public Cliente atualizar(Long id, ClienteForm form) {
        Cliente cliente = buscarPorId(id);

        DadosClienteValidados dados = cadeiaDeValidacao.validar(
                form.nome(), form.email(), form.telefone(), form.cep(), id);

        cliente.atualizarDados(dados.nome(), dados.email(), dados.telefone(), obterEndereco(dados.cep()));

        Cliente salvo = clienteRepository.save(cliente);
        publisher.publishEvent(new ClienteAtualizadoEvent(salvo));
        return salvo;
    }

    @Override
    @Transactional
    public void remover(Long id) {
        Cliente cliente = buscarPorId(id);
        clienteRepository.delete(cliente);
        publisher.publishEvent(new ClienteRemovidoEvent(cliente.getId(), cliente.getNome(), cliente.getEmail()));
    }

    @Override
    public Endereco consultarCep(String cep) {
        return cepService.buscar(cep);
    }

    // endereco ja conhecido e reaproveitado; so vai na rede na primeira vez
    private Endereco obterEndereco(String cep) {
        return enderecoRepository.findById(cep)
                .orElseGet(() -> enderecoRepository.save(cepService.buscar(cep)));
    }
}
