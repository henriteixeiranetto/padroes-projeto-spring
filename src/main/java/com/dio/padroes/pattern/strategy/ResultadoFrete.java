package com.dio.padroes.pattern.strategy;

import com.dio.padroes.domain.model.ModalidadeFrete;

import java.math.BigDecimal;

public record ResultadoFrete(
        ModalidadeFrete modalidade,
        String rotulo,
        String descricao,
        BigDecimal valor,
        Integer prazoDias,
        boolean disponivel,
        String observacao,
        String estrategia) {

    public static ResultadoFrete indisponivel(ModalidadeFrete modalidade, String motivo, String estrategia) {
        return new ResultadoFrete(modalidade, modalidade.getRotulo(), modalidade.getDescricao(),
                null, null, false, motivo, estrategia);
    }
}
