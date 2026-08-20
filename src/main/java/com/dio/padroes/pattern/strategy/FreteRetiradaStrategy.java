package com.dio.padroes.pattern.strategy;

import com.dio.padroes.domain.model.ModalidadeFrete;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class FreteRetiradaStrategy implements FreteStrategy {

    @Override
    public ModalidadeFrete modalidade() {
        return ModalidadeFrete.RETIRADA;
    }

    @Override
    public ResultadoFrete calcular(PedidoFrete pedido) {
        return new ResultadoFrete(modalidade(), modalidade().getRotulo(), modalidade().getDescricao(),
                BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP), 1, true,
                "Separação em 1 dia útil - retirada na loja da Av. Paulista", getClass().getSimpleName());
    }
}
