package com.dio.padroes.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "cliente")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, unique = true)
    private String email;

    private String telefone;

    // eager de proposito: o endereco sempre aparece junto do cliente e open-in-view esta desligado
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "endereco_cep")
    private Endereco endereco;

    private LocalDateTime criadoEm;
    private LocalDateTime atualizadoEm;

    protected Cliente() {
    }

    private Cliente(Builder builder) {
        this.id = builder.id;
        this.nome = builder.nome;
        this.email = builder.email;
        this.telefone = builder.telefone;
        this.endereco = builder.endereco;
    }

    public static Builder builder() {
        return new Builder();
    }

    @PrePersist
    void aoCriar() {
        this.criadoEm = LocalDateTime.now();
        this.atualizadoEm = this.criadoEm;
    }

    @PreUpdate
    void aoAtualizar() {
        this.atualizadoEm = LocalDateTime.now();
    }

    public void atualizarDados(String nome, String email, String telefone, Endereco endereco) {
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
        this.endereco = endereco;
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public String getTelefone() {
        return telefone;
    }

    public Endereco getEndereco() {
        return endereco;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public LocalDateTime getAtualizadoEm() {
        return atualizadoEm;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        return obj instanceof Cliente outro && id != null && Objects.equals(id, outro.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Cliente[id=%s, nome=%s, email=%s]".formatted(id, nome, email);
    }

    public static final class Builder {

        private Long id;
        private String nome;
        private String email;
        private String telefone;
        private Endereco endereco;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder nome(String nome) {
            this.nome = nome;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder telefone(String telefone) {
            this.telefone = telefone;
            return this;
        }

        public Builder endereco(Endereco endereco) {
            this.endereco = endereco;
            return this;
        }

        public Cliente build() {
            if (nome == null || nome.isBlank()) {
                throw new IllegalStateException("nome e obrigatorio");
            }
            if (email == null || email.isBlank()) {
                throw new IllegalStateException("email e obrigatorio");
            }
            if (endereco == null) {
                throw new IllegalStateException("endereco e obrigatorio");
            }
            return new Cliente(this);
        }
    }
}
