package com.dio.padroes.api.controller;

import com.dio.padroes.api.dto.EnderecoResponse;
import com.dio.padroes.api.mapper.ClienteMapper;
import com.dio.padroes.service.ClienteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cep")
@Tag(name = "2. CEP", description = "Consulta de endereço - vitrine do Adapter e do Decorator")
public class CepController {

    private final ClienteService clienteService;

    public CepController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @GetMapping("/{cep}")
    @Operation(summary = "Consulta um CEP",
            description = "Passa pelo Decorator: cache -> ViaCEP -> tabela offline (nessa ordem)")
    public EnderecoResponse consultar(
            @Parameter(description = "CEP com ou sem máscara", example = "01001-000")
            @PathVariable String cep) {
        return ClienteMapper.toResponse(clienteService.consultarCep(cep));
    }
}
