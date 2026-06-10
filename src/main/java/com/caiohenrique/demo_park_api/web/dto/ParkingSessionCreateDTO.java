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
@Schema(name = "ParkingSessionCreateDTO", description = "Dados necessários para registrar a entrada de um veículo no estacionamento")
public class ParkingSessionCreateDTO {

    /**
     * DTO responsável por transportar os dados do veículo
     * e do cliente para registrar uma nova sessão de estacionamento.
     */

    @Schema(description = "Placa do veículo", example = "ABC-1234")
    @NotBlank(message = "A placa é obrigatória")
    @Size(min = 8, max = 8)
    @Pattern(regexp = "([A-Z]{3}-\\d{4})|([A-Z]{3}\\d[A-Z]\\d{2})", message = "Placa inválida")
    private String licensePlate;

    @Schema(description = "Marca do veículo", example = "Toyota")
    @NotBlank(message = "A marca é obrigatória")
    private String brand;

    @Schema(description = "Modelo do veículo", example = "Corolla")
    @NotBlank(message = "O modelo é obrigatório")
    private String model;

    @Schema(description = "Cor do veículo", example = "Prata")
    @NotBlank(message = "A cor é obrigatória")
    private String color;

    @Schema(description = "CPF do cliente previamente cadastrado", example = "12345678909")
    @NotBlank(message = "O CPF é obrigatório")
    @CPF(message = "CPF inválido")
    private String clientCpf;

}
