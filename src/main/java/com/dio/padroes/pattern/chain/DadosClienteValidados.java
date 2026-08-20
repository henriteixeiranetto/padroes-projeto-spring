package com.dio.padroes.pattern.chain;

import java.util.List;

public record DadosClienteValidados(String nome, String email, String telefone, String cep, List<String> trilha) {
}
