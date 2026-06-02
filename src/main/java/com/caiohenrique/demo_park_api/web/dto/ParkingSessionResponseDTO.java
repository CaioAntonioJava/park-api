package com.caiohenrique.demo_park_api.web.dto;

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

    private LocalDateTime checkIn;

    private LocalDateTime checkOut;

    private String spotCode;

    private BigDecimal parkingFee;

    private BigDecimal discount;

}
