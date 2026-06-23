package com.caiohenrique.demo_park_api.web.dto;

import com.caiohenrique.demo_park_api.enums.SpotStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Dados retornados com as informações da vaga de estacionamento")
public class ParkingSpotResponseDTO {

    @Schema(
            description = "Identificador único da vaga",
            example = "1"
    )
    private Long id;

    @Schema(
            description = "Código identificador da vaga de estacionamento",
            example = "A001"
    )
    private String spotCode;

    @Schema(
            description = "Situação atual da vaga de estacionamento",
            example = "LIVRE"
    )
    private SpotStatus spotStatus;
}