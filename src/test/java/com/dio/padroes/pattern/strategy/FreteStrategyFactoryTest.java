package com.dio.padroes.pattern.strategy;

import com.dio.padroes.domain.model.ModalidadeFrete;
import com.dio.padroes.pattern.factory.FreteStrategyFactory;
import com.dio.padroes.pattern.singleton.TabelaDeTarifas;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class FreteStrategyFactoryTest {

    private static final PedidoFrete PARA_PORTO_ALEGRE = new PedidoFrete("90010-150", new BigDecimal("2.5"));
    private static final PedidoFrete PARA_SAO_PAULO = new PedidoFrete("01001-000", new BigDecimal("2.5"));

    private FreteStrategyFactory fabrica;

    @BeforeEach
    void montar() {
        TabelaDeTarifas tarifas = new TabelaDeTarifas();
        fabrica = new FreteStrategyFactory(List.of(
                new FretePacStrategy(tarifas),
                new FreteSedexStrategy(tarifas),
                new FreteExpressoStrategy(tarifas),
                new FreteRetiradaStrategy()));
    }

    @Test
    @DisplayName("entrega a estrategia correspondente a modalidade pedida")
    void deveResolverEstrategiaPorModalidade() {
        assertThat(fabrica.de(ModalidadeFrete.SEDEX)).isInstanceOf(FreteSedexStrategy.class);
        assertThat(fabrica.de(ModalidadeFrete.PAC)).isInstanceOf(FretePacStrategy.class);
        assertThat(fabrica.modalidadesSuportadas()).hasSize(4);
    }

    @Test
    @DisplayName("SEDEX custa mais e chega antes do que o PAC para o mesmo destino")
    void sedexDeveSerMaisCaroEMaisRapido() {
        ResultadoFrete pac = fabrica.calcular(ModalidadeFrete.PAC, PARA_PORTO_ALEGRE);
        ResultadoFrete sedex = fabrica.calcular(ModalidadeFrete.SEDEX, PARA_PORTO_ALEGRE);

        assertThat(sedex.valor()).isGreaterThan(pac.valor());
        assertThat(sedex.prazoDias()).isLessThan(pac.prazoDias());
    }

    @Test
    @DisplayName("retirada na loja nao tem custo")
    void retiradaDeveSerGratuita() {
        ResultadoFrete retirada = fabrica.calcular(ModalidadeFrete.RETIRADA, PARA_PORTO_ALEGRE);

        assertThat(retirada.valor()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(retirada.disponivel()).isTrue();
    }

    @Test
    @DisplayName("expresso so atende a regiao do centro de distribuicao")
    void expressoDeveSerRestrito() {
        assertThat(fabrica.calcular(ModalidadeFrete.EXPRESSO, PARA_SAO_PAULO).disponivel()).isTrue();

        ResultadoFrete foraDeArea = fabrica.calcular(ModalidadeFrete.EXPRESSO, PARA_PORTO_ALEGRE);
        assertThat(foraDeArea.disponivel()).isFalse();
        assertThat(foraDeArea.valor()).isNull();
        assertThat(foraDeArea.observacao()).contains("São Paulo");
    }

    @Test
    @DisplayName("simular todas devolve as disponiveis primeiro, da mais barata para a mais cara")
    void deveOrdenarSimulacao() {
        List<ResultadoFrete> opcoes = fabrica.simularTodas(PARA_PORTO_ALEGRE);

        assertThat(opcoes).hasSize(4);
        assertThat(opcoes.get(0).modalidade()).isEqualTo(ModalidadeFrete.RETIRADA);
        assertThat(opcoes.get(opcoes.size() - 1).disponivel()).isFalse();
        assertThat(opcoes).extracting(ResultadoFrete::estrategia).doesNotContainNull();
    }

    @Test
    @DisplayName("mesmo destino, quatro regras completamente diferentes")
    void cadaEstrategiaCalculaDoSeuJeito() {
        List<ResultadoFrete> disponiveis = fabrica.simularTodas(PARA_SAO_PAULO).stream()
                .filter(ResultadoFrete::disponivel)
                .toList();

        assertThat(disponiveis).hasSize(4);
        assertThat(disponiveis).extracting(ResultadoFrete::valor).doesNotHaveDuplicates();
    }
}
