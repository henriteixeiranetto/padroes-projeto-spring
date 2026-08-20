package com.dio.padroes.service;

import com.dio.padroes.domain.model.Endereco;

// tres implementacoes: ViaCEP, tabela offline e o cache que embrulha as duas
public interface CepService {

    Endereco buscar(String cep);

    String identificacao();
}
