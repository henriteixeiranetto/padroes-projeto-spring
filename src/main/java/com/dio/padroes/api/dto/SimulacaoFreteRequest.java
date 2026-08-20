package com.dio.padroes.api.dto;

import com.dio.padroes.domain.model.ModalidadeFrete;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;

@Schema(name = "SimulacaoFreteRequest", description = "Entrada da simulação de frete")
public record SimulacaoFreteRequest(

        @Schema(example = "90010-150")
        @NotBlank(message = "cep: é obrigatório")
        String cep,

        @Schema(example = "2.5", description = "Peso em quilos")
        @DecimalMin(value = "0.1", message = "pesoKg: mínimo de 0.1 kg")
        @DecimalMax(value = "100.0", message = "pesoKg: máximo de 100 kg")
        BigDecimal pesoKg,

        @Schema(description = "Opcional. Sem modalidade, todas são simuladas e comparadas")
        ModalidadeFrete modalidade) {
}
