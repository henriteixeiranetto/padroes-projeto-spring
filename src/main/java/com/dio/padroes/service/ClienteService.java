package com.dio.padroes.service;

import com.dio.padroes.domain.model.Cliente;
import com.dio.padroes.domain.model.Endereco;

import java.util.List;

public interface ClienteService {

    List<Cliente> listar(String filtroNome);

    Cliente buscarPorId(Long id);

    Cliente criar(ClienteForm form);

    Cliente atualizar(Long id, ClienteForm form);

    void remover(Long id);

    Endereco consultarCep(String cep);
}
