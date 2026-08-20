package com.dio.padroes.support;

import com.dio.padroes.api.dto.PadraoResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CatalogoDePadroes {

    private static final List<PadraoResponse> PADROES = List.of(

            new PadraoResponse("Singleton", "Criacional",
                    "A tabela de preços e prazos é consultada em todo cálculo de frete; recriá-la a cada chamada seria desperdício.",
                    "Um bean do Spring, cujo escopo padrão já é singleton, guarda as tarifas. Unicidade sem getInstance(), sem estado global e sem perder testabilidade.",
                    List.of("pattern.singleton.TabelaDeTarifas"),
                    "Chame GET /api/padroes/singleton várias vezes: o instanciaId nunca muda."),

            new PadraoResponse("Strategy", "Comportamental",
                    "Cada modalidade de entrega calcula preço e prazo de um jeito. Um switch central cresceria a cada modalidade nova.",
                    "Uma classe por modalidade implementando FreteStrategy. Adicionar uma modalidade é criar uma classe: nenhuma existente muda.",
                    List.of("pattern.strategy.FreteStrategy", "pattern.strategy.FretePacStrategy",
                            "pattern.strategy.FreteSedexStrategy", "pattern.strategy.FreteExpressoStrategy",
                            "pattern.strategy.FreteRetiradaStrategy"),
                    "POST /api/fretes/simular sem informar modalidade: o campo estrategia mostra qual classe calculou cada linha."),

            new PadraoResponse("Factory", "Criacional",
                    "Quem simula o frete conhece apenas a modalidade, não as classes que a implementam.",
                    "A fábrica recebe do Spring todas as implementações de FreteStrategy e monta um mapa de modalidade para estratégia.",
                    List.of("pattern.factory.FreteStrategyFactory"),
                    "GET /api/fretes/modalidades lista o que a fábrica registrou sozinha na inicialização."),

            new PadraoResponse("Facade", "Estrutural",
                    "Cadastrar um cliente envolve validar, consultar CEP, gravar endereço, gravar cliente e notificar. Espalhar isso pelo controller seria acoplamento puro.",
                    "Uma fachada oferece o cadastro como uma única operação e esconde os quatro subsistemas por trás dela.",
                    List.of("pattern.facade.ClienteFacade", "service.ClienteService"),
                    "POST /api/clientes: uma chamada, cinco subsistemas envolvidos."),

            new PadraoResponse("Chain of Responsibility", "Comportamental",
                    "As regras de cadastro (nome, e-mail único, telefone, CEP) tendem a virar um método gigante cheio de ifs.",
                    "Cada regra é um elo independente. O Spring injeta os elos já ordenados e a cadeia os encadeia na inicialização; criar uma regra nova não altera nenhum arquivo existente.",
                    List.of("pattern.chain.CadeiaDeValidacaoCliente", "pattern.chain.ValidacaoClienteHandler",
                            "pattern.chain.NomeHandler", "pattern.chain.EmailHandler",
                            "pattern.chain.TelefoneHandler", "pattern.chain.CepHandler"),
                    "Poste um cliente com nome curto, e-mail repetido e CEP torto: a resposta 422 traz todos os erros de uma vez."),

            new PadraoResponse("Observer", "Comportamental",
                    "Depois de cadastrar um cliente é preciso notificar e auditar, e amanhã, quem sabe, alimentar um relatório.",
                    "A facade apenas publica um evento; auditoria e notificação se inscrevem. Publicador e observadores não se conhecem.",
                    List.of("pattern.observer.EventoDeCliente", "pattern.observer.ClienteCriadoEvent",
                            "pattern.observer.AuditoriaListener", "pattern.observer.NotificacaoListener"),
                    "Cadastre um cliente e veja GET /api/padroes/eventos."),

            new PadraoResponse("Template Method", "Comportamental",
                    "Enviar e-mail e enviar SMS seguem o mesmo roteiro, mas alguns passos mudam.",
                    "A classe abstrata fixa o roteiro em um método final e deixa os passos variáveis para as subclasses (o SMS, por exemplo, encurta o corpo em 140 caracteres).",
                    List.of("pattern.template.NotificacaoTemplate", "pattern.template.EmailNotificacao",
                            "pattern.template.SmsNotificacao"),
                    "Cadastre um cliente com telefone e compare as duas mensagens em GET /api/padroes/notificacoes."),

            new PadraoResponse("Adapter", "Estrutural",
                    "O ViaCEP fala a língua dele (localidade em vez de cidade, campo de erro textual, CEP sem máscara) e o nosso domínio fala outra.",
                    "Um adaptador traduz a resposta externa para a entidade Endereco. Se o contrato do ViaCEP mudar, só este arquivo muda.",
                    List.of("pattern.adapter.ViaCepAdapter", "pattern.adapter.ViaCepResposta",
                            "pattern.adapter.ViaCepClient"),
                    "GET /api/cep/01001-000 devolve o formato do nosso domínio, não o do ViaCEP."),

            new PadraoResponse("Decorator", "Estrutural",
                    "Queremos cache e tolerância a falha na consulta de CEP sem sujar a classe que faz a integração.",
                    "Um decorador implementa CepService e embrulha outro CepService, acrescentando cache e queda para o modo offline. Marcado como @Primary, é injetado no lugar do original de forma transparente.",
                    List.of("pattern.decorator.CepServiceComCache", "service.ViaCepService",
                            "service.CepOfflineService"),
                    "Consulte o mesmo CEP duas vezes e compare o campo fonte (VIACEP na primeira, CACHE na segunda). Os números estão em GET /api/padroes/decorator."),

            new PadraoResponse("Builder", "Criacional",
                    "Endereco tem oito campos quase todos opcionais; um construtor telescópico seria ilegível.",
                    "Builders fluentes, com as invariantes verificadas no método build().",
                    List.of("domain.model.Endereco.Builder", "domain.model.Cliente.Builder"),
                    "Veja o código da facade: o cliente é montado campo a campo, com a intenção explícita em cada linha."),

            new PadraoResponse("Repository", "Arquitetural",
                    "O domínio não deveria conhecer SQL nem EntityManager.",
                    "Interfaces do Spring Data declaram as consultas; o framework gera a implementação (que é, por sua vez, um Proxy).",
                    List.of("domain.repository.ClienteRepository", "domain.repository.EnderecoRepository"),
                    "Abra o H2 console em /h2-console e veja as tabelas criadas a partir das entidades."),

            new PadraoResponse("DTO", "Arquitetural",
                    "Expor entidades JPA na API amarra o contrato público ao banco de dados.",
                    "Records de entrada e saída, com um mapper explícito entre as camadas.",
                    List.of("api.dto.ClienteRequest", "api.dto.ClienteResponse", "api.mapper.ClienteMapper"),
                    "Compare o JSON de POST /api/clientes com a entidade Cliente: o id e as datas nunca são aceitos na entrada.")
    );

    public List<PadraoResponse> listar() {
        return PADROES;
    }
}
