package com.dio.padroes.pattern.singleton;

import com.dio.padroes.domain.model.ModalidadeFrete;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class TabelaDeTarifas {

    // id aleatorio so para provar em /api/padroes/singleton que a instancia e sempre a mesma
    private final String instanciaId = UUID.randomUUID().toString().substring(0, 8);
    private final LocalDateTime criadaEm = LocalDateTime.now();
    private final AtomicLong consultas = new AtomicLong();

    private final Map<ModalidadeFrete, BigDecimal> tarifaBase = Map.of(
            ModalidadeFrete.PAC, new BigDecimal("12.90"),
            ModalidadeFrete.SEDEX, new BigDecimal("24.90"),
            ModalidadeFrete.EXPRESSO, new BigDecimal("39.90"),
            ModalidadeFrete.RETIRADA, BigDecimal.ZERO);

    private final Map<ModalidadeFrete, BigDecimal> custoPorKg = Map.of(
            ModalidadeFrete.PAC, new BigDecimal("2.40"),
            ModalidadeFrete.SEDEX, new BigDecimal("3.90"),
            ModalidadeFrete.EXPRESSO, new BigDecimal("5.50"),
            ModalidadeFrete.RETIRADA, BigDecimal.ZERO);

    // multiplicador de distancia a partir do centro de distribuicao (Grande Sao Paulo)
    private final Map<Integer, BigDecimal> fatorDistancia = Map.ofEntries(
            Map.entry(0, new BigDecimal("0.0")),
            Map.entry(1, new BigDecimal("0.5")),
            Map.entry(2, new BigDecimal("1.0")),
            Map.entry(3, new BigDecimal("1.0")),
            Map.entry(4, new BigDecimal("2.0")),
            Map.entry(5, new BigDecimal("2.5")),
            Map.entry(6, new BigDecimal("3.0")),
            Map.entry(7, new BigDecimal("2.0")),
            Map.entry(8, new BigDecimal("1.0")),
            Map.entry(9, new BigDecimal("1.5")));

    private final Map<Integer, Integer> diasAdicionais = Map.ofEntries(
            Map.entry(0, 0),
            Map.entry(1, 1),
            Map.entry(2, 1),
            Map.entry(3, 2),
            Map.entry(4, 4),
            Map.entry(5, 5),
            Map.entry(6, 6),
            Map.entry(7, 3),
            Map.entry(8, 2),
            Map.entry(9, 3));

    public BigDecimal tarifaBase(ModalidadeFrete modalidade) {
        consultas.incrementAndGet();
        return tarifaBase.getOrDefault(modalidade, BigDecimal.ZERO);
    }

    public BigDecimal custoPorKg(ModalidadeFrete modalidade) {
        return custoPorKg.getOrDefault(modalidade, BigDecimal.ZERO);
    }

    public BigDecimal fatorDistancia(int regiaoPostal) {
        return fatorDistancia.getOrDefault(regiaoPostal, new BigDecimal("2.0"));
    }

    public int diasAdicionais(int regiaoPostal) {
        return diasAdicionais.getOrDefault(regiaoPostal, 5);
    }

    public boolean atendeExpresso(int regiaoPostal) {
        return regiaoPostal <= 1;
    }

    public String getInstanciaId() {
        return instanciaId;
    }

    public LocalDateTime getCriadaEm() {
        return criadaEm;
    }

    public long getConsultas() {
        return consultas.get();
    }
}
