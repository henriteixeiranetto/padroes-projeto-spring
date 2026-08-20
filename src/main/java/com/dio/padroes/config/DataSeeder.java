package com.dio.padroes.config;

import com.dio.padroes.domain.model.Cliente;
import com.dio.padroes.domain.model.Endereco;
import com.dio.padroes.domain.repository.ClienteRepository;
import com.dio.padroes.domain.repository.EnderecoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@ConditionalOnProperty(name = "app.seed.enabled", havingValue = "true", matchIfMissing = true)
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final ClienteRepository clienteRepository;
    private final EnderecoRepository enderecoRepository;

    public DataSeeder(ClienteRepository clienteRepository, EnderecoRepository enderecoRepository) {
        this.clienteRepository = clienteRepository;
        this.enderecoRepository = enderecoRepository;
    }

    // grava direto pelos repositorios para nao encher a linha do tempo de eventos na inicializacao
    @Override
    @Transactional
    public void run(String... args) {
        if (clienteRepository.count() > 0) {
            return;
        }

        Endereco se = salvar(Endereco.builder()
                .cep("01001-000").logradouro("Praça da Sé").bairro("Sé")
                .localidade("São Paulo").uf("SP").ddd("11").fonte("SEED").build());

        Endereco rioBranco = salvar(Endereco.builder()
                .cep("20040-020").logradouro("Avenida Rio Branco").bairro("Centro")
                .localidade("Rio de Janeiro").uf("RJ").ddd("21").fonte("SEED").build());

        Endereco andradas = salvar(Endereco.builder()
                .cep("90010-150").logradouro("Rua dos Andradas").bairro("Centro Histórico")
                .localidade("Porto Alegre").uf("RS").ddd("51").fonte("SEED").build());

        clienteRepository.save(Cliente.builder()
                .nome("Maria Silva").email("maria.silva@empresa.com.br")
                .telefone("(11) 98765-4321").endereco(se).build());

        clienteRepository.save(Cliente.builder()
                .nome("João Pereira").email("joao.pereira@empresa.com.br")
                .telefone("(21) 99876-5432").endereco(rioBranco).build());

        clienteRepository.save(Cliente.builder()
                .nome("Ana Souza").email("ana.souza@empresa.com.br")
                .endereco(andradas).build());

        log.info("Banco populado com {} clientes de exemplo", clienteRepository.count());
    }

    private Endereco salvar(Endereco endereco) {
        return enderecoRepository.save(endereco);
    }
}
