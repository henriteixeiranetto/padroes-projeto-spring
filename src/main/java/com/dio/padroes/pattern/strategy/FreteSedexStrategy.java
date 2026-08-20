package com.dio.padroes.pattern.strategy;

import com.dio.padroes.domain.model.ModalidadeFrete;
import com.dio.padroes.pattern.singleton.TabelaDeTarifas;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class FreteSedexStrategy implements FreteStrategy {

    private static final BigDecimal CUSTO_POR_FATOR_DISTANCIA = new BigDecimal("7.00");
    private static final int PRAZO_MINIMO_DIAS = 1;

    private final TabelaDeTarifas tarifas;

    public FreteSedexStrategy(TabelaDeTarifas tarifas) {
        this.tarifas = tarifas;
    }

    @Override
    public ModalidadeFrete modalidade() {
        return ModalidadeFrete.SEDEX;
    }

    @Override
    public ResultadoFrete calcular(PedidoFrete pedido) {
        int regiao = pedido.regiao();
        BigDecimal valor = tarifas.tarifaBase(modalidade())
                .add(tarifas.custoPorKg(modalidade()).multiply(pedido.pesoKg()))
                .add(tarifas.fatorDistancia(regiao).multiply(CUSTO_POR_FATOR_DISTANCIA))
                .setScale(2, RoundingMode.HALF_UP);

        int prazo = Math.max(PRAZO_MINIMO_DIAS, PRAZO_MINIMO_DIAS + tarifas.diasAdicionais(regiao) - 1);

        return new ResultadoFrete(modalidade(), modalidade().getRotulo(), modalidade().getDescricao(),
                valor, prazo, true, "Entrega prioritária com rastreio", getClass().getSimpleName());
    }
}
