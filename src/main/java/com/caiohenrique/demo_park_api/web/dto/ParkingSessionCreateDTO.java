package com.caiohenrique.demo_park_api.web.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.br.CPF;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(
        name = "ParkingSessionCreateDTO",
        description = "Dados necessários para registrar a entrada de um veículo e iniciar uma sessão de estacionamento"
)
public class ParkingSessionCreateDTO {


    @NotBlank(message = "A placa é obrigatória")
    @Size(min = 7, max = 7)
    @Pattern(regexp = "^[A-Z]{3}[0-9][A-Z0-9][0-9]{2}$", message = "Placa inválida")
    @Schema(
            description = "Placa do veículo sem caracteres especiais no formato brasileiro",
            example = "BRA1234",
            pattern = "^[A-Z]{3}[0-9][A-Z0-9][0-9]{2}$",
            minLength = 7,
            maxLength = 7,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String licensePlate;

    @NotBlank(message = "A marca é obrigatória")
    @Schema(
            description = "Marca do veículo",
            example = "Toyota",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String brand;

    @NotBlank(message = "O modelo é obrigatório")
    @Schema(
            description = "Modelo do veículo",
            example = "Corolla",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String model;

    @NotBlank(message = "A cor é obrigatória")
    @Schema(
            description = "Cor predominante do veículo",
            example = "Prata",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String color;

    @NotBlank(message = "O CPF é obrigatório")
    @CPF(message = "CPF inválido")
    @Size(min = 11, max = 11)
    @Schema(
            description = "CPF do cliente previamente cadastrado",
            example = "12345678909",
            pattern = "\\d{11}",
            minLength = 11,
            maxLength = 11,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String clientCpf;

}
