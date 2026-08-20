package com.dio.padroes.pattern.strategy;

import com.dio.padroes.domain.model.ModalidadeFrete;
import com.dio.padroes.pattern.singleton.TabelaDeTarifas;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class FretePacStrategy implements FreteStrategy {

    private static final BigDecimal CUSTO_POR_FATOR_DISTANCIA = new BigDecimal("4.50");
    private static final int PRAZO_MINIMO_DIAS = 4;

    private final TabelaDeTarifas tarifas;

    public FretePacStrategy(TabelaDeTarifas tarifas) {
        this.tarifas = tarifas;
    }

    @Override
    public ModalidadeFrete modalidade() {
        return ModalidadeFrete.PAC;
    }

    @Override
    public ResultadoFrete calcular(PedidoFrete pedido) {
        int regiao = pedido.regiao();
        BigDecimal valor = tarifas.tarifaBase(modalidade())
                .add(tarifas.custoPorKg(modalidade()).multiply(pedido.pesoKg()))
                .add(tarifas.fatorDistancia(regiao).multiply(CUSTO_POR_FATOR_DISTANCIA))
                .setScale(2, RoundingMode.HALF_UP);

        int prazo = PRAZO_MINIMO_DIAS + tarifas.diasAdicionais(regiao);

        return new ResultadoFrete(modalidade(), modalidade().getRotulo(), modalidade().getDescricao(),
                valor, prazo, true, "Melhor custo-benefício", getClass().getSimpleName());
    }
}
