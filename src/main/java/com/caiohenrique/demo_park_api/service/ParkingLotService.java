package com.caiohenrique.demo_park_api.service;


import com.caiohenrique.demo_park_api.entity.Client;
import com.caiohenrique.demo_park_api.entity.ParkingSession;
import com.caiohenrique.demo_park_api.entity.ParkingSpot;
import com.caiohenrique.demo_park_api.enums.SpotStatus;
import com.caiohenrique.demo_park_api.exception.ActiveParkingSessionAlreadyExistsException;
import com.caiohenrique.demo_park_api.parking.ParkingDiscountCalculator;
import com.caiohenrique.demo_park_api.parking.ParkingFeeCalculator;
import com.caiohenrique.demo_park_api.parking.ParkingReceiptGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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

        if (parkingSessionService.existsOpenSessionByLicensePlate(
                parkingSession.getLicensePlate())) {

            throw new ActiveParkingSessionAlreadyExistsException(
                    String.format(
                            "A placa %s já possui uma sessão de estacionamento ativa.", parkingSession.getLicensePlate()
                    ));
        }

        Client client = clientService.findByCpf(parkingSession.getClient().getCpf());
        parkingSession.setClient(client);

        ParkingSpot parkingSpot = parkingSpotService.findAvailableParkingSpot();
        parkingSpot.setSpotStatus(SpotStatus.OCUPADA);

        parkingSession.setParkingSpot(parkingSpot);

        parkingSession.setCheckIn(LocalDateTime.now());

        parkingSession.setReceiptNumber(ParkingReceiptGenerator.generate());

        return parkingSessionService.save(parkingSession);
    }

    @Transactional
    public ParkingSession checkOut(String receipt) {
        ParkingSession parkingSession = parkingSessionService.findOpenSessionByReceipt(receipt);

        LocalDateTime exitDate = LocalDateTime.now();

        long completedParkingSessions = parkingSessionService.countCompletedParkingSessions(parkingSession.getClient().getCpf());
        parkingSession.setCheckOut(exitDate);
        parkingSession.getParkingSpot().setSpotStatus(SpotStatus.LIVRE);

        BigDecimal parkingFee = ParkingFeeCalculator.calculate(parkingSession.getCheckIn(), exitDate);
        parkingSession.setParkingFee(parkingFee);


        BigDecimal discount = ParkingDiscountCalculator.calculateDiscount(parkingFee, completedParkingSessions);
        parkingSession.setDiscount(discount);

        return parkingSessionService.save(parkingSession);
    }
}
