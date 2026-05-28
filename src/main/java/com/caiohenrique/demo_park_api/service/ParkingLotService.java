package com.caiohenrique.demo_park_api.service;


import com.caiohenrique.demo_park_api.entity.Client;
import com.caiohenrique.demo_park_api.entity.ParkingSession;
import com.caiohenrique.demo_park_api.entity.ParkingSpot;
import com.caiohenrique.demo_park_api.enums.SpotStatus;
import com.caiohenrique.demo_park_api.parking.ParkingReceiptService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ParkingLotService {

    /**
     * Classe responsável por centralizar as regras de negócio
     * relacionadas ao funcionamento do estacionamento,
     * como controle de vagas, disponibilidade, ocupação
     * e demais operações administrativas.
     */

    private final ParkingSessionService parkingSessionService;
    private final ClientService clientService;
    private final ParkingSpotService parkingSpotService;

    @Transactional
    public ParkingSession checkIn(ParkingSession parkingSession) {
        Client client = clientService.findByCpf(parkingSession.getClient().getCpf());
        parkingSession.setClient(client);

        ParkingSpot parkingSpot = parkingSpotService.findAvailableParkingSpot();
        parkingSpot.setSpotStatus(SpotStatus.OCUPADA);

        parkingSession.setParkingSpot(parkingSpot);

        parkingSession.setCheckIn(LocalDateTime.now());

        parkingSession.setReceiptNumber(ParkingReceiptService.generateReceipt());

        return parkingSessionService.save(parkingSession);
    }
}
