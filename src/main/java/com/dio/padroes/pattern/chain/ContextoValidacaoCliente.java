package com.dio.padroes.pattern.chain;

import java.util.ArrayList;
import java.util.List;

public class ContextoValidacaoCliente {

    private String nome;
    private String email;
    private String telefone;
    private String cep;

    private final Long idClienteEmEdicao;

    private final List<String> erros = new ArrayList<>();
    private final List<String> trilha = new ArrayList<>();

    public ContextoValidacaoCliente(String nome, String email, String telefone, String cep, Long idClienteEmEdicao) {
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
        this.cep = cep;
        this.idClienteEmEdicao = idClienteEmEdicao;
    }

    public void adicionarErro(String mensagem) {
        erros.add(mensagem);
    }

    public void registrarPasso(String passo) {
        trilha.add(passo);
    }

    public boolean temErros() {
        return !erros.isEmpty();
    }

    public List<String> getErros() {
        return List.copyOf(erros);
    }

    public List<String> getTrilha() {
        return List.copyOf(trilha);
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public Long getIdClienteEmEdicao() {
        return idClienteEmEdicao;
    }
}
