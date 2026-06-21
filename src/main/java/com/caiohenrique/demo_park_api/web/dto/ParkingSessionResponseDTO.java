package com.caiohenrique.demo_park_api.web.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
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
public class ParkingSessionResponseDTO {

    /**
     * DTO responsável por transportar os dados de entrada,
     * saída, vaga e cobrança de uma sessão de estacionamento.
     */

    private String licensePlate;

    private String brand;

    private String model;

    private String color;

    private String clientCpf;

    private String receiptNumber;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime checkIn;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime checkOut;

    private String spotCode;

    private BigDecimal parkingFee;

    private BigDecimal discount;

}
