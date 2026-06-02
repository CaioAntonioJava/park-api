package com.caiohenrique.demo_park_api.service;

import com.caiohenrique.demo_park_api.entity.ParkingSession;
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
}
