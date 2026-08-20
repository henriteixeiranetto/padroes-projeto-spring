package com.dio.padroes.api.controller;

import com.dio.padroes.api.dto.ClienteRequest;
import com.dio.padroes.api.dto.ClienteResponse;
import com.dio.padroes.api.mapper.ClienteMapper;
import com.dio.padroes.domain.model.Cliente;
import com.dio.padroes.service.ClienteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/clientes")
@Tag(name = "1. Clientes", description = "Cadastro de clientes - vitrine da Facade, da Chain of Responsibility e do Observer")
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @GetMapping
    @Operation(summary = "Lista os clientes", description = "Opcionalmente filtra por parte do nome")
    public List<ClienteResponse> listar(
            @Parameter(description = "Trecho do nome para filtrar", example = "maria")
            @RequestParam(required = false) String nome) {
        return clienteService.listar(nome).stream()
                .map(ClienteMapper::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca um cliente pelo id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cliente encontrado"),
            @ApiResponse(responseCode = "404", description = "Cliente inexistente", content = @io.swagger.v3.oas.annotations.media.Content)
    })
    public ClienteResponse buscar(@PathVariable Long id) {
        return ClienteMapper.toResponse(clienteService.buscarPorId(id));
    }

    @PostMapping
    @Operation(summary = "Cadastra um cliente",
            description = "O endereço é resolvido automaticamente a partir do CEP (ViaCEP, cache ou modo offline)")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Cliente cadastrado"),
            @ApiResponse(responseCode = "400", description = "Campos obrigatórios ausentes", content = @io.swagger.v3.oas.annotations.media.Content),
            @ApiResponse(responseCode = "422", description = "Regra de negócio violada (e-mail duplicado, CEP inválido...)", content = @io.swagger.v3.oas.annotations.media.Content)
    })
    public ResponseEntity<ClienteResponse> criar(@Valid @RequestBody ClienteRequest request) {
        Cliente cliente = clienteService.criar(ClienteMapper.toForm(request));
        return ResponseEntity
                .created(URI.create("/api/clientes/" + cliente.getId()))
                .body(ClienteMapper.toResponse(cliente));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza um cliente existente")
    public ClienteResponse atualizar(@PathVariable Long id, @Valid @RequestBody ClienteRequest request) {
        return ClienteMapper.toResponse(clienteService.atualizar(id, ClienteMapper.toForm(request)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remove um cliente")
    @ApiResponse(responseCode = "204", description = "Cliente removido")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        clienteService.remover(id);
        return ResponseEntity.noContent().build();
    }
}
