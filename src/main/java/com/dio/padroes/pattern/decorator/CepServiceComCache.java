package com.dio.padroes.pattern.decorator;

import com.dio.padroes.api.exception.IntegracaoIndisponivelException;
import com.dio.padroes.api.exception.RegraDeNegocioException;
import com.dio.padroes.config.AppProperties;
import com.dio.padroes.domain.model.Endereco;
import com.dio.padroes.service.CepService;
import com.dio.padroes.support.Ceps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

// acrescenta cache e queda para o modo offline em volta do ViaCepService
@Primary
@Component
public class CepServiceComCache implements CepService {

    private static final Logger log = LoggerFactory.getLogger(CepServiceComCache.class);

    public static final String FONTE_CACHE = "CACHE";

    private final CepService origem;
    private final CepService fallback;
    private final boolean fallbackHabilitado;

    private final Map<String, Endereco> cache = new ConcurrentHashMap<>();
    private final AtomicLong consultas = new AtomicLong();
    private final AtomicLong acertosDeCache = new AtomicLong();
    private final AtomicLong chamadasExternas = new AtomicLong();
    private final AtomicLong quedasParaOffline = new AtomicLong();

    public CepServiceComCache(@Qualifier("viaCepService") CepService origem,
                              @Qualifier("cepOfflineService") CepService fallback,
                              AppProperties properties) {
        this.origem = origem;
        this.fallback = fallback;
        this.fallbackHabilitado = properties.viaCep().fallbackOffline();
    }

    @Override
    public Endereco buscar(String cep) {
        if (!Ceps.valido(cep)) {
            throw new RegraDeNegocioException("CEP inválido: informe 8 dígitos (ex.: 01001-000)");
        }

        consultas.incrementAndGet();
        String chave = Ceps.formatar(cep);

        Endereco emCache = cache.get(chave);
        if (emCache != null) {
            acertosDeCache.incrementAndGet();
            log.debug("CEP {} servido pelo cache do Decorator", chave);
            return copiar(emCache, FONTE_CACHE);
        }

        Endereco encontrado = consultarOrigem(chave);
        cache.put(chave, encontrado);
        return copiar(encontrado, encontrado.getFonte());
    }

    private Endereco consultarOrigem(String chave) {
        try {
            chamadasExternas.incrementAndGet();
            return origem.buscar(chave);
        } catch (IntegracaoIndisponivelException e) {
            if (!fallbackHabilitado) {
                throw e;
            }
            quedasParaOffline.incrementAndGet();
            log.warn("ViaCEP indisponivel ({}). Usando a estrategia offline para o CEP {}", e.getMessage(), chave);
            return fallback.buscar(chave);
        }
    }

    @Override
    public String identificacao() {
        return "CepServiceComCache (Decorator sobre %s)".formatted(origem.identificacao());
    }

    // copia defensiva: o cache guarda uma instancia e quem chama recebe outra
    private Endereco copiar(Endereco origem, String fonte) {
        return Endereco.builder()
                .cep(origem.getCep())
                .logradouro(origem.getLogradouro())
                .complemento(origem.getComplemento())
                .bairro(origem.getBairro())
                .localidade(origem.getLocalidade())
                .uf(origem.getUf())
                .ibge(origem.getIbge())
                .ddd(origem.getDdd())
                .fonte(fonte)
                .build();
    }

    public void limparCache() {
        cache.clear();
    }

    public Estatisticas estatisticas() {
        return new Estatisticas(
                consultas.get(),
                acertosDeCache.get(),
                chamadasExternas.get(),
                quedasParaOffline.get(),
                cache.size());
    }

    public record Estatisticas(long consultas,
                               long acertosDeCache,
                               long chamadasExternas,
                               long quedasParaOffline,
                               int cepsEmCache) {
    }
}
