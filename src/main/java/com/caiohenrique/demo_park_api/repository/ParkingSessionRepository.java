package com.caiohenrique.demo_park_api.repository;

import com.caiohenrique.demo_park_api.entity.ParkingSession;
import com.caiohenrique.demo_park_api.repository.projection.ParkingSessionProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ParkingSessionRepository extends JpaRepository<ParkingSession, Long> {

    Optional<ParkingSession> findByReceiptNumberAndCheckOutIsNull(String receipt);

    long countByClientCpfAndCheckOutIsNotNull(String cpf);


    boolean existsByLicensePlateAndCheckOutIsNull(String licensePlate);

    @Query("""
            select
                ps.licensePlate as licensePlate,
                ps.brand as brand,
                ps.model as model,
                ps.color as color,
                ps.client.cpf as clientCpf,
                ps.receiptNumber as receiptNumber,
                ps.checkIn as checkIn,
                ps.checkOut as checkOut,
                ps.parkingSpot.spotCode as spotCode,
                ps.parkingFee as parkingFee,
                ps.discount as discount
            from ParkingSession ps
            where ps.client.cpf = :cpf
            """)
    Page<ParkingSessionProjection> findAllByClientCpf(
            @Param("cpf") String cpf,
            Pageable pageable
    );

    @Query("""
            select
                ps.licensePlate as licensePlate,
                ps.brand as brand,
                ps.model as model,
                ps.color as color,
                ps.client.cpf as clientCpf,
                ps.receiptNumber as receiptNumber,
                ps.checkIn as checkIn,
                ps.checkOut as checkOut,
                ps.parkingSpot.spotCode as spotCode,
                ps.parkingFee as parkingFee,
                ps.discount as discount
            from ParkingSession ps
            where ps.client.user.id = :userId
            """)
    Page<ParkingSessionProjection> findAllByClientUserId(
            @Param("userId") Long userId,
            Pageable pageable
    );
}
