package com.caiohenrique.demo_park_api.web.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Dados retornados com as informações de uma sessão de estacionamento")
public class ParkingSessionResponseDTO {

    @Schema(
            description = "Placa do veículo estacionado",
            example = "BRA1234"
    )
    private String licensePlate;

    @Schema(
            description = "Marca do veículo",
            example = "Toyota"
    )
    private String brand;

    @Schema(
            description = "Modelo do veículo",
            example = "Corolla"
    )
    private String model;

    @Schema(
            description = "Cor predominante do veículo",
            example = "Prata"
    )
    private String color;

    @Schema(
            description = "CPF do cliente responsável pela sessão",
            example = "12345678909"
    )
    private String clientCpf;

    @Schema(
            description = "Número do recibo gerado para a sessão",
            example = "REC-20260623-0001"
    )
    private String receiptNumber;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(
            description = "Data e hora de entrada do veículo no estacionamento",
            example = "2026-06-23 10:30:00"
    )
    private LocalDateTime checkIn;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(
            description = "Data e hora de saída do veículo do estacionamento",
            example = "2026-06-23 12:45:00"
    )
    private LocalDateTime checkOut;

    @Schema(
            description = "Código da vaga utilizada pelo veículo",
            example = "A001"
    )
    private String spotCode;

    @Schema(
            description = "Valor total cobrado pelo período de estacionamento",
            example = "15.50"
    )
    private BigDecimal parkingFee;

    @Schema(
            description = "Valor de desconto aplicado na cobrança",
            example = "5.00"
    )
    private BigDecimal discount;
}