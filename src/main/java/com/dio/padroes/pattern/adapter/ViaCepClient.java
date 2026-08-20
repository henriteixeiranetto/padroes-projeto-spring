package com.dio.padroes.pattern.adapter;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

// HTTP interface do Spring 6 no lugar do OpenFeign: uma dependencia a menos
@HttpExchange(accept = "application/json")
public interface ViaCepClient {

    @GetExchange("/{cep}/json/")
    ViaCepResposta consultar(@PathVariable("cep") String cep);
}
