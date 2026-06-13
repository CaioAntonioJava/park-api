package com.caiohenrique.demo_park_api.service;

import com.caiohenrique.demo_park_api.entity.ParkingSession;
import com.caiohenrique.demo_park_api.exception.EntityNotFoundException;
import com.caiohenrique.demo_park_api.repository.ParkingSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ParkingSessionService {

    private final ParkingSessionRepository parkingSessionRepository;

    /* Responsável por cadastrar uma nova sessão de estacionamento. */
    @Transactional
    public ParkingSession save(ParkingSession parkingSession) {

        return parkingSessionRepository.save(parkingSession);
    }

    @Transactional(readOnly = true)
    public ParkingSession findOpenSessionByReceipt(String receipt) {
        return parkingSessionRepository.findByReceiptNumberAndCheckOutIsNull(receipt).orElseThrow(
                () -> new EntityNotFoundException(String.format("""
                        Recibo '%s' não encontrado no sistema ou check-out já realizado
                        """, receipt))
        );
    }

    @Transactional(readOnly = true)
    public long countCompletedParkingSessions(String cpf) {
        return parkingSessionRepository.countByClientCpfAndCheckOutIsNotNull(cpf);
    }

    @Transactional(readOnly = true)
    public boolean existsOpenSessionByLicensePlate(String licensePlate) {
        return parkingSessionRepository.existsByLicensePlateAndCheckOutIsNull(licensePlate);
    }
}