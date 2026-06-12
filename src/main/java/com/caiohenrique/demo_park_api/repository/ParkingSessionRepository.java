package com.caiohenrique.demo_park_api.repository;

import com.caiohenrique.demo_park_api.entity.ParkingSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ParkingSessionRepository extends JpaRepository<ParkingSession, Long> {

    Optional<ParkingSession> findByReceiptNumberAndCheckOutIsNull(String receipt);

    long countByClientCpfAndCheckOutIsNotNull(String cpf);
}
