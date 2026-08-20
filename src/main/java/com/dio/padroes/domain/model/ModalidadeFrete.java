package com.dio.padroes.domain.model;

public enum ModalidadeFrete {

    PAC("PAC", "Entrega econômica dos Correios"),
    SEDEX("SEDEX", "Entrega rápida com rastreio"),
    EXPRESSO("Expresso", "Entrega no mesmo dia (apenas capitais atendidas)"),
    RETIRADA("Retirada na loja", "Sem custo de frete, retirada no balcão");

    private final String rotulo;
    private final String descricao;

    ModalidadeFrete(String rotulo, String descricao) {
        this.rotulo = rotulo;
        this.descricao = descricao;
    }

    public String getRotulo() {
        return rotulo;
    }

    public String getDescricao() {
        return descricao;
    }
}
