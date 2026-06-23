package com.caiohenrique.demo_park_api.repository.projection;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@JsonPropertyOrder({
        "clientCpf",
        "receiptNumber",
        "spotCode",
        "licensePlate",
        "brand",
        "model",
        "color",
        "checkIn",
        "checkOut",
        "parkingFee",
        "discount"
})
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Dados retornados com as informações de uma sessão de estacionamento")
public interface ParkingSessionProjection {

    @Schema(
            description = "Número do recibo gerado para a sessão",
            example = "REC-20260623-0001"
    )
    String getReceiptNumber();


    @Schema(
            description = "Placa do veículo estacionado",
            example = "BRA1234"
    )
    String getLicensePlate();


    @Schema(
            description = "Marca do veículo",
            example = "Toyota"
    )
    String getBrand();


    @Schema(
            description = "Modelo do veículo",
            example = "Corolla"
    )
    String getModel();


    @Schema(
            description = "Cor predominante do veículo",
            example = "Prata"
    )
    String getColor();


    @Schema(
            description = "CPF do cliente responsável pela sessão",
            example = "12345678909",
            pattern = "\\d{11}",
            minLength = 11,
            maxLength = 11
    )
    String getClientCpf();


    @Schema(
            description = "Código da vaga utilizada",
            example = "A001"
    )
    String getSpotCode();


    @Schema(
            description = "Código da vaga retornado pelo relacionamento com a vaga de estacionamento",
            example = "A001"
    )
    String getParkingSpotSpotCode();


    @Schema(
            description = "Data e hora de entrada do veículo",
            example = "2026-06-23 10:30:00"
    )
    LocalDateTime getCheckIn();


    @Schema(
            description = "Data e hora de saída do veículo",
            example = "2026-06-23 12:45:00"
    )
    LocalDateTime getCheckOut();


    @Schema(
            description = "Valor total cobrado pela permanência",
            example = "15.50"
    )
    BigDecimal getParkingFee();


    @Schema(
            description = "Valor de desconto aplicado na cobrança",
            example = "5.00"
    )
    BigDecimal getDiscount();
}