package com.dio.padroes.pattern.strategy;

import com.dio.padroes.domain.model.ModalidadeFrete;

public interface FreteStrategy {

    ModalidadeFrete modalidade();

    ResultadoFrete calcular(PedidoFrete pedido);
}
