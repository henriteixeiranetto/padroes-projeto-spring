package com.dio.padroes.api.controller;

import com.dio.padroes.api.dto.ModalidadeResponse;
import com.dio.padroes.api.dto.SimulacaoFreteRequest;
import com.dio.padroes.domain.model.ModalidadeFrete;
import com.dio.padroes.pattern.factory.FreteStrategyFactory;
import com.dio.padroes.pattern.strategy.PedidoFrete;
import com.dio.padroes.pattern.strategy.ResultadoFrete;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/fretes")
@Tag(name = "3. Frete", description = "Simulação de entrega - vitrine do Strategy, do Factory e do Singleton")
public class FreteController {

    private final FreteStrategyFactory freteStrategyFactory;

    public FreteController(FreteStrategyFactory freteStrategyFactory) {
        this.freteStrategyFactory = freteStrategyFactory;
    }

    @PostMapping("/simular")
    @Operation(summary = "Simula o frete para um CEP",
            description = "Informe a modalidade para calcular apenas uma, ou omita para comparar todas")
    public List<ResultadoFrete> simular(@Valid @RequestBody SimulacaoFreteRequest request) {
        PedidoFrete pedido = new PedidoFrete(request.cep(), request.pesoKg());
        if (request.modalidade() == null) {
            return freteStrategyFactory.simularTodas(pedido);
        }
        return List.of(freteStrategyFactory.calcular(request.modalidade(), pedido));
    }

    @GetMapping("/modalidades")
    @Operation(summary = "Lista as modalidades suportadas e a estratégia que implementa cada uma")
    public List<ModalidadeResponse> modalidades() {
        return freteStrategyFactory.modalidadesSuportadas().stream()
                .map(this::descrever)
                .toList();
    }

    private ModalidadeResponse descrever(ModalidadeFrete modalidade) {
        return new ModalidadeResponse(
                modalidade.name(),
                modalidade.getRotulo(),
                modalidade.getDescricao(),
                freteStrategyFactory.de(modalidade).getClass().getSimpleName());
    }
}
