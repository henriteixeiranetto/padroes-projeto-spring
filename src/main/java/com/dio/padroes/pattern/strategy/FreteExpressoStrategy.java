package com.dio.padroes.pattern.strategy;

import com.dio.padroes.domain.model.ModalidadeFrete;
import com.dio.padroes.pattern.singleton.TabelaDeTarifas;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class FreteExpressoStrategy implements FreteStrategy {

    private final TabelaDeTarifas tarifas;

    public FreteExpressoStrategy(TabelaDeTarifas tarifas) {
        this.tarifas = tarifas;
    }

    @Override
    public ModalidadeFrete modalidade() {
        return ModalidadeFrete.EXPRESSO;
    }

    @Override
    public ResultadoFrete calcular(PedidoFrete pedido) {
        int regiao = pedido.regiao();
        // fora da area devolve indisponivel em vez de estourar excecao, senao quebra a simulacao das outras
        if (!tarifas.atendeExpresso(regiao)) {
            return ResultadoFrete.indisponivel(modalidade(),
                    "Disponível apenas para CEPs do estado de São Paulo (região postal 0 e 1)",
                    getClass().getSimpleName());
        }

        BigDecimal valor = tarifas.tarifaBase(modalidade())
                .add(tarifas.custoPorKg(modalidade()).multiply(pedido.pesoKg()))
                .setScale(2, RoundingMode.HALF_UP);

        return new ResultadoFrete(modalidade(), modalidade().getRotulo(), modalidade().getDescricao(),
                valor, 0, true, "Chega hoje para pedidos até as 14h", getClass().getSimpleName());
    }
}
