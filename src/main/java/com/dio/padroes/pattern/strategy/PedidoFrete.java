package com.dio.padroes.pattern.strategy;

import com.dio.padroes.support.Ceps;

import java.math.BigDecimal;

public record PedidoFrete(String cep, BigDecimal pesoKg) {

    public PedidoFrete {
        if (!Ceps.valido(cep)) {
            throw new IllegalArgumentException("CEP invalido: " + cep);
        }
        // peso invalido nao derruba a simulacao, assume 1 kg
        if (pesoKg == null || pesoKg.signum() <= 0) {
            pesoKg = new BigDecimal("1.0");
        }
        cep = Ceps.formatar(cep);
    }

    public int regiao() {
        return Ceps.regiao(cep);
    }
}
