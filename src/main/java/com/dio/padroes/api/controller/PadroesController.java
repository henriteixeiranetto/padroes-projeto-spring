package com.dio.padroes.api.controller;

import com.dio.padroes.api.dto.PadraoResponse;
import com.dio.padroes.pattern.chain.CadeiaDeValidacaoCliente;
import com.dio.padroes.pattern.decorator.CepServiceComCache;
import com.dio.padroes.pattern.observer.LinhaDoTempo;
import com.dio.padroes.pattern.singleton.TabelaDeTarifas;
import com.dio.padroes.pattern.template.CaixaDeSaida;
import com.dio.padroes.pattern.template.Notificacao;
import com.dio.padroes.support.CatalogoDePadroes;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.context.ApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
@RequestMapping("/api/padroes")
@Tag(name = "4. Padroes", description = "Endpoints didáticos - veja cada padrão funcionando de verdade")
public class PadroesController {

    private final CatalogoDePadroes catalogo;
    private final CadeiaDeValidacaoCliente cadeiaDeValidacao;
    private final TabelaDeTarifas tabelaDeTarifas;
    private final CepServiceComCache cepServiceComCache;
    private final LinhaDoTempo linhaDoTempo;
    private final CaixaDeSaida caixaDeSaida;
    private final ApplicationContext context;

    public PadroesController(CatalogoDePadroes catalogo,
                             CadeiaDeValidacaoCliente cadeiaDeValidacao,
                             TabelaDeTarifas tabelaDeTarifas,
                             CepServiceComCache cepServiceComCache,
                             LinhaDoTempo linhaDoTempo,
                             CaixaDeSaida caixaDeSaida,
                             ApplicationContext context) {
        this.catalogo = catalogo;
        this.cadeiaDeValidacao = cadeiaDeValidacao;
        this.tabelaDeTarifas = tabelaDeTarifas;
        this.cepServiceComCache = cepServiceComCache;
        this.linhaDoTempo = linhaDoTempo;
        this.caixaDeSaida = caixaDeSaida;
        this.context = context;
    }

    @GetMapping
    @Operation(summary = "Catálogo dos padrões aplicados no projeto")
    public List<PadraoResponse> listar() {
        return catalogo.listar();
    }

    @GetMapping("/chain")
    @Operation(summary = "Elos da cadeia de validação, na ordem em que são executados")
    public List<EloResponse> cadeia() {
        AtomicInteger ordem = new AtomicInteger(1);
        return cadeiaDeValidacao.getElos().stream()
                .map(elo -> new EloResponse(ordem.getAndIncrement(), elo.nome(), elo.descricao()))
                .toList();
    }

    @GetMapping("/singleton")
    @Operation(summary = "Prova de que o bean de tarifas é único",
            description = "Busca o bean duas vezes no contexto e compara as referências")
    public SingletonResponse singleton() {
        TabelaDeTarifas primeira = context.getBean(TabelaDeTarifas.class);
        TabelaDeTarifas segunda = context.getBean(TabelaDeTarifas.class);
        return new SingletonResponse(
                tabelaDeTarifas.getInstanciaId(),
                tabelaDeTarifas.getCriadaEm(),
                tabelaDeTarifas.getConsultas(),
                primeira == segunda,
                "Duas buscas no contexto devolvem a mesma referência: escopo singleton do Spring.");
    }

    @GetMapping("/decorator")
    @Operation(summary = "Números do decorador de CEP (cache, chamadas externas, quedas para o modo offline)")
    public DecoratorResponse decorator() {
        return new DecoratorResponse(cepServiceComCache.identificacao(), cepServiceComCache.estatisticas());
    }

    @GetMapping("/eventos")
    @Operation(summary = "Linha do tempo dos eventos observados")
    public List<LinhaDoTempo.Registro> eventos() {
        return linhaDoTempo.listar();
    }

    @GetMapping("/notificacoes")
    @Operation(summary = "Caixa de saída das notificações geradas pelo Template Method")
    public List<Notificacao> notificacoes() {
        return caixaDeSaida.listar();
    }

    @DeleteMapping("/historico")
    @Operation(summary = "Zera eventos, notificações e cache de CEP",
            description = "Útil para demonstrar os padrões do zero, sem reiniciar a aplicação")
    public ResponseEntity<Void> limparHistorico() {
        linhaDoTempo.limpar();
        caixaDeSaida.limpar();
        cepServiceComCache.limparCache();
        return ResponseEntity.noContent().build();
    }

    public record EloResponse(int ordem, String elo, String verifica) {
    }

    public record SingletonResponse(String instanciaId,
                                    LocalDateTime criadaEm,
                                    long consultasAsTarifas,
                                    boolean mesmaInstancia,
                                    String explicacao) {
    }

    public record DecoratorResponse(String implementacaoAtiva, CepServiceComCache.Estatisticas estatisticas) {
    }
}
