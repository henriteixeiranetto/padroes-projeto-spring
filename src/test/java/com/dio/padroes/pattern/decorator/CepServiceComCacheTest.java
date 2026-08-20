package com.dio.padroes.pattern.decorator;

import com.dio.padroes.api.exception.IntegracaoIndisponivelException;
import com.dio.padroes.api.exception.RegraDeNegocioException;
import com.dio.padroes.config.AppProperties;
import com.dio.padroes.domain.model.Endereco;
import com.dio.padroes.service.CepService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CepServiceComCacheTest {

    private static AppProperties propriedades(boolean fallbackOffline) {
        return new AppProperties(
                new AppProperties.ViaCep("http://localhost:1", Duration.ofMillis(200), Duration.ofMillis(200), fallbackOffline),
                new AppProperties.Seed(false));
    }

    private static Endereco endereco(String cep, String fonte) {
        return Endereco.builder().cep(cep).localidade("São Paulo").uf("SP").fonte(fonte).build();
    }

    @Test
    @DisplayName("consulta a origem uma vez e serve as demais pelo cache")
    void deveUsarCacheNaSegundaConsulta() {
        AtomicInteger chamadas = new AtomicInteger();
        CepService origem = new CepServiceFake(cep -> {
            chamadas.incrementAndGet();
            return endereco(cep, "VIACEP");
        });

        CepServiceComCache decorador = new CepServiceComCache(origem, origem, propriedades(true));

        assertThat(decorador.buscar("01001-000").getFonte()).isEqualTo("VIACEP");
        assertThat(decorador.buscar("01001000").getFonte()).isEqualTo("CACHE");
        assertThat(chamadas.get()).isEqualTo(1);

        CepServiceComCache.Estatisticas estatisticas = decorador.estatisticas();
        assertThat(estatisticas.consultas()).isEqualTo(2);
        assertThat(estatisticas.acertosDeCache()).isEqualTo(1);
        assertThat(estatisticas.cepsEmCache()).isEqualTo(1);
    }

    @Test
    @DisplayName("cai para a estrategia offline quando a integracao falha")
    void deveUsarFallbackQuandoIntegracaoCai() {
        CepService origem = new CepServiceFake(cep -> {
            throw new IntegracaoIndisponivelException("ViaCEP fora do ar", new RuntimeException());
        });
        CepService offline = new CepServiceFake(cep -> endereco(cep, "OFFLINE"));

        CepServiceComCache decorador = new CepServiceComCache(origem, offline, propriedades(true));

        assertThat(decorador.buscar("01001-000").getFonte()).isEqualTo("OFFLINE");
        assertThat(decorador.estatisticas().quedasParaOffline()).isEqualTo(1);
    }

    @Test
    @DisplayName("propaga a falha quando o modo offline esta desligado")
    void devePropagarFalhaSemFallback() {
        CepService origem = new CepServiceFake(cep -> {
            throw new IntegracaoIndisponivelException("ViaCEP fora do ar", new RuntimeException());
        });
        CepService offline = new CepServiceFake(cep -> endereco(cep, "OFFLINE"));

        CepServiceComCache decorador = new CepServiceComCache(origem, offline, propriedades(false));

        assertThatThrownBy(() -> decorador.buscar("01001-000"))
                .isInstanceOf(IntegracaoIndisponivelException.class);
    }

    @Test
    @DisplayName("recusa CEP fora do formato antes de qualquer consulta")
    void deveRecusarCepInvalido() {
        AtomicInteger chamadas = new AtomicInteger();
        CepService origem = new CepServiceFake(cep -> {
            chamadas.incrementAndGet();
            return endereco(cep, "VIACEP");
        });

        CepServiceComCache decorador = new CepServiceComCache(origem, origem, propriedades(true));

        assertThatThrownBy(() -> decorador.buscar("123"))
                .isInstanceOf(RegraDeNegocioException.class);
        assertThat(chamadas.get()).isZero();
    }

    private record CepServiceFake(java.util.function.Function<String, Endereco> comportamento) implements CepService {

        @Override
        public Endereco buscar(String cep) {
            return comportamento.apply(cep);
        }

        @Override
        public String identificacao() {
            return "CepServiceFake";
        }
    }
}
