package com.dio.padroes.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.Objects;

@Entity
@Table(name = "endereco")
public class Endereco {

    @Id
    @Column(length = 9)
    private String cep;

    private String logradouro;
    private String complemento;
    private String bairro;
    private String localidade;

    @Column(length = 2)
    private String uf;

    private String ibge;
    private String ddd;

    @Column(length = 20)
    private String fonte;

    protected Endereco() {
    }

    private Endereco(Builder builder) {
        this.cep = builder.cep;
        this.logradouro = builder.logradouro;
        this.complemento = builder.complemento;
        this.bairro = builder.bairro;
        this.localidade = builder.localidade;
        this.uf = builder.uf;
        this.ibge = builder.ibge;
        this.ddd = builder.ddd;
        this.fonte = builder.fonte;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getCep() {
        return cep;
    }

    public String getLogradouro() {
        return logradouro;
    }

    public String getComplemento() {
        return complemento;
    }

    public String getBairro() {
        return bairro;
    }

    public String getLocalidade() {
        return localidade;
    }

    public String getUf() {
        return uf;
    }

    public String getIbge() {
        return ibge;
    }

    public String getDdd() {
        return ddd;
    }

    public String getFonte() {
        return fonte;
    }

    public void setFonte(String fonte) {
        this.fonte = fonte;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        return obj instanceof Endereco outro && Objects.equals(cep, outro.cep);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cep);
    }

    @Override
    public String toString() {
        return "Endereco[cep=%s, localidade=%s/%s]".formatted(cep, localidade, uf);
    }

    // builder porque sao 8 campos e quase todos opcionais
    public static final class Builder {

        private String cep;
        private String logradouro;
        private String complemento;
        private String bairro;
        private String localidade;
        private String uf;
        private String ibge;
        private String ddd;
        private String fonte;

        public Builder cep(String cep) {
            this.cep = cep;
            return this;
        }

        public Builder logradouro(String logradouro) {
            this.logradouro = logradouro;
            return this;
        }

        public Builder complemento(String complemento) {
            this.complemento = complemento;
            return this;
        }

        public Builder bairro(String bairro) {
            this.bairro = bairro;
            return this;
        }

        public Builder localidade(String localidade) {
            this.localidade = localidade;
            return this;
        }

        public Builder uf(String uf) {
            this.uf = uf;
            return this;
        }

        public Builder ibge(String ibge) {
            this.ibge = ibge;
            return this;
        }

        public Builder ddd(String ddd) {
            this.ddd = ddd;
            return this;
        }

        public Builder fonte(String fonte) {
            this.fonte = fonte;
            return this;
        }

        public Endereco build() {
            if (cep == null || cep.isBlank()) {
                throw new IllegalStateException("CEP e obrigatorio para construir um Endereco");
            }
            return new Endereco(this);
        }
    }
}
