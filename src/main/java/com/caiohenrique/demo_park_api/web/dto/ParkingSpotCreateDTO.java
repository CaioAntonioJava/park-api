package com.caiohenrique.demo_park_api.web.dto;

import com.caiohenrique.demo_park_api.enums.SpotStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Dados necessários para cadastro de uma vaga de estacionamento")
public class ParkingSpotCreateDTO {

    @NotBlank
    @Size(min = 4, max = 4)
    @Pattern(regexp = "^[A-Z0-9]{4}$")
    @Schema(
            description = "Código identificador da vaga composto por 4 caracteres alfanuméricos maiúsculos",
            example = "A001",
            pattern = "^[A-Z0-9]{4}$",
            minLength = 4,
            maxLength = 4,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String spotCode;

    @NotNull
    @Schema(
            description = "Situação atual da vaga de estacionamento",
            example = "LIVRE",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private SpotStatus spotStatus;
}
