package com.caiohenrique.demo_park_api.repository.projection;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;

@JsonPropertyOrder({"id", "name", "cpf"})
@Schema(description = "Dados resumidos do cliente retornados em consultas")
public interface ClientProjection {

    @Schema(
            description = "Identificador único do cliente",
            example = "1"
    )
    Long getId();

    @Schema(
            description = "Nome completo do cliente",
            example = "João da Silva"
    )
    String getName();

    @Schema(
            description = "CPF do cliente conforme padrão brasileiro",
            example = "12345678909",
            pattern = "\\d{11}",
            minLength = 11,
            maxLength = 11
    )
    String getCpf();
}