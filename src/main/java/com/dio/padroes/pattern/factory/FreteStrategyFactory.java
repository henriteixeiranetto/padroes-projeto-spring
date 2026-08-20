package com.dio.padroes.pattern.factory;

import com.dio.padroes.api.exception.RecursoNaoEncontradoException;
import com.dio.padroes.domain.model.ModalidadeFrete;
import com.dio.padroes.pattern.strategy.FreteStrategy;
import com.dio.padroes.pattern.strategy.PedidoFrete;
import com.dio.padroes.pattern.strategy.ResultadoFrete;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class FreteStrategyFactory {

    private static final Logger log = LoggerFactory.getLogger(FreteStrategyFactory.class);

    private final Map<ModalidadeFrete, FreteStrategy> estrategias = new EnumMap<>(ModalidadeFrete.class);

    // o Spring injeta todas as implementacoes: estrategia nova se registra sozinha
    public FreteStrategyFactory(List<FreteStrategy> implementacoes) {
        for (FreteStrategy estrategia : implementacoes) {
            FreteStrategy anterior = estrategias.put(estrategia.modalidade(), estrategia);
            if (anterior != null) {
                throw new IllegalStateException("Duas estrategias para a modalidade " + estrategia.modalidade());
            }
        }
        log.info("Estrategias de frete registradas: {}", estrategias.keySet());
    }

    public FreteStrategy de(ModalidadeFrete modalidade) {
        FreteStrategy estrategia = estrategias.get(modalidade);
        if (estrategia == null) {
            throw new RecursoNaoEncontradoException("Modalidade de frete não suportada: " + modalidade);
        }
        return estrategia;
    }

    public ResultadoFrete calcular(ModalidadeFrete modalidade, PedidoFrete pedido) {
        return de(modalidade).calcular(pedido);
    }

    public List<ResultadoFrete> simularTodas(PedidoFrete pedido) {
        return estrategias.values().stream()
                .map(estrategia -> estrategia.calcular(pedido))
                .sorted(Comparator
                        .comparing(ResultadoFrete::disponivel).reversed()
                        .thenComparing(resultado -> resultado.valor() == null ? Double.MAX_VALUE : resultado.valor().doubleValue()))
                .toList();
    }

    public Set<ModalidadeFrete> modalidadesSuportadas() {
        return estrategias.keySet();
    }
}
