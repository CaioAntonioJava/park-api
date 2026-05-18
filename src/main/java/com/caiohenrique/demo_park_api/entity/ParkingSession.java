package com.caiohenrique.demo_park_api.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entidade responsável por representar a permanência
 * de um veículo em uma vaga de estacionamento.
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "parking_sessions")
@EntityListeners(AuditingEntityListener.class)
public class ParkingSession extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /* Dados do recibo */

    @Column(name = "receipt_number", nullable = false, unique = true, length = 15)
    private String receiptNumber;

    /* Dados do veículo associado à sessão de estacionamento. */

    @Column(name = "license_plate", nullable = false, length = 8)
    private String licensePlate;

    @Column(name = "brand", nullable = false, length = 45)
    private String brand;

    @Column(name = "model", nullable = false, length = 45)
    private String model;

    @Column(name = "color", nullable = false, length = 45)
    private String color;

    @Column(name = "check_in", nullable = false)
    private LocalDateTime checkIn;

    @Column(name = "check_out")
    private LocalDateTime checkOut;

    /* Valores da permanência */

    @Column(name = "parking_fee", precision = 7, scale = 2)
    private BigDecimal parkingFee;

    @Column(name = "discount", precision = 7, scale = 2)
    private BigDecimal discount;

    /* Relacionamento entre cliente e vaga de estacionamento */

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parking_spot_id", nullable = false)
    private ParkingSpot parkingSpot;

    /* Equals e HashCode */

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ParkingSession that = (ParkingSession) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
