package com.caiohenrique.demo_park_api.service;

import com.caiohenrique.demo_park_api.entity.Client;
import com.caiohenrique.demo_park_api.entity.ParkingSession;
import com.caiohenrique.demo_park_api.entity.ParkingSpot;
import com.caiohenrique.demo_park_api.enums.SpotStatus;
import com.caiohenrique.demo_park_api.exception.ActiveParkingSessionAlreadyExistsException;
import com.caiohenrique.demo_park_api.exception.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ParkingLotServiceTest {

    @Mock
    private ParkingSessionService parkingSessionService;

    @Mock
    private ClientService clientService;

    @Mock
    private ParkingSpotService parkingSpotService;

    @InjectMocks
    private ParkingLotService parkingLotService;

    private ParkingSession parkingSession;
    private Client client;
    private ParkingSpot parkingSpot;

    @BeforeEach
    void setUp() {
        client = new Client();
        client.setId(1L);
        client.setName("João da Silva");
        client.setCpf("12345678909");

        parkingSpot = new ParkingSpot();
        parkingSpot.setId(1L);
        parkingSpot.setSpotCode("A001");
        parkingSpot.setSpotStatus(SpotStatus.LIVRE);

        parkingSession = new ParkingSession();
        parkingSession.setLicensePlate("BRA1234");
        parkingSession.setBrand("Toyota");
        parkingSession.setModel("Corolla");
        parkingSession.setColor("Prata");
        parkingSession.setClient(client);
    }

    @Test
    void checkIn_withValidData_shouldCreateSession() {
        when(parkingSessionService.existsOpenSessionByLicensePlate("BRA1234")).thenReturn(false);
        when(clientService.findByCpf("12345678909")).thenReturn(client);
        when(parkingSpotService.findAvailableParkingSpot()).thenReturn(parkingSpot);
        when(parkingSessionService.save(any(ParkingSession.class))).thenAnswer(returnsFirstArg());

        ParkingSession result = parkingLotService.checkIn(parkingSession);

        assertNotNull(result);
        assertEquals("BRA1234", result.getLicensePlate());
        assertEquals(client, result.getClient());
        assertEquals(parkingSpot, result.getParkingSpot());
        assertEquals(SpotStatus.OCUPADA, parkingSpot.getSpotStatus());
        assertNotNull(result.getCheckIn());
        assertNotNull(result.getReceiptNumber());
        assertTrue(result.getReceiptNumber().startsWith("REC-"));

        verify(parkingSessionService).existsOpenSessionByLicensePlate("BRA1234");
        verify(clientService).findByCpf("12345678909");
        verify(parkingSpotService).findAvailableParkingSpot();
        verify(parkingSessionService).save(any(ParkingSession.class));
    }

    @Test
    void checkIn_withActiveSessionForLicensePlate_shouldThrowActiveParkingSessionAlreadyExistsException() {
        when(parkingSessionService.existsOpenSessionByLicensePlate("BRA1234")).thenReturn(true);

        assertThrows(ActiveParkingSessionAlreadyExistsException.class,
                () -> parkingLotService.checkIn(parkingSession));

        verify(parkingSessionService).existsOpenSessionByLicensePlate("BRA1234");
        verify(clientService, never()).findByCpf(anyString());
        verify(parkingSpotService, never()).findAvailableParkingSpot();
    }

    @Test
    void checkIn_withNonExistingClient_shouldThrowEntityNotFoundException() {
        when(parkingSessionService.existsOpenSessionByLicensePlate("BRA1234")).thenReturn(false);
        when(clientService.findByCpf("12345678909"))
                .thenThrow(new EntityNotFoundException("Cliente não encontrado"));

        assertThrows(EntityNotFoundException.class,
                () -> parkingLotService.checkIn(parkingSession));
    }

    @Test
    void checkIn_withNoAvailableSpot_shouldThrowEntityNotFoundException() {
        when(parkingSessionService.existsOpenSessionByLicensePlate("BRA1234")).thenReturn(false);
        when(clientService.findByCpf("12345678909")).thenReturn(client);
        when(parkingSpotService.findAvailableParkingSpot())
                .thenThrow(new EntityNotFoundException("Nenhuma vaga livre foi encontrada"));

        assertThrows(EntityNotFoundException.class,
                () -> parkingLotService.checkIn(parkingSession));
    }

    @Test
    void checkOut_withValidReceipt_shouldCompleteSession() {
        // Setup: session with check-in already done
        parkingSession.setCheckIn(LocalDateTime.of(2026, 7, 15, 8, 0));
        parkingSession.setParkingSpot(parkingSpot);
        parkingSession.setReceiptNumber("REC-20260715080000-ABCD1234");

        when(parkingSessionService.findOpenSessionByReceipt("REC-20260715080000-ABCD1234"))
                .thenReturn(parkingSession);
        when(parkingSessionService.countCompletedParkingSessions("12345678909")).thenReturn(10L);
        when(parkingSessionService.save(any(ParkingSession.class))).thenAnswer(returnsFirstArg());

        ParkingSession result = parkingLotService.checkOut("REC-20260715080000-ABCD1234");

        assertNotNull(result);
        assertNotNull(result.getCheckOut());
        assertEquals(SpotStatus.LIVRE, parkingSpot.getSpotStatus());
        assertNotNull(result.getParkingFee());
        assertTrue(result.getParkingFee().compareTo(BigDecimal.ZERO) > 0);
        assertNotNull(result.getDiscount());
        assertTrue(result.getDiscount().compareTo(BigDecimal.ZERO) > 0);

        verify(parkingSessionService).findOpenSessionByReceipt("REC-20260715080000-ABCD1234");
        verify(parkingSessionService).countCompletedParkingSessions("12345678909");
        verify(parkingSessionService).save(any(ParkingSession.class));
    }

    @Test
    void checkOut_withNonExistingReceipt_shouldThrowEntityNotFoundException() {
        when(parkingSessionService.findOpenSessionByReceipt("INVALID"))
                .thenThrow(new EntityNotFoundException("Recibo não encontrado"));

        assertThrows(EntityNotFoundException.class,
                () -> parkingLotService.checkOut("INVALID"));
    }

    @Test
    void checkOut_withNoDiscount_shouldCalculateCorrectly() {
        parkingSession.setCheckIn(LocalDateTime.of(2026, 7, 15, 8, 0));
        parkingSession.setParkingSpot(parkingSpot);
        parkingSession.setReceiptNumber("REC-20260715080000-ABCD1234");

        when(parkingSessionService.findOpenSessionByReceipt("REC-20260715080000-ABCD1234"))
                .thenReturn(parkingSession);
        when(parkingSessionService.countCompletedParkingSessions("12345678909")).thenReturn(5L);
        when(parkingSessionService.save(any(ParkingSession.class))).thenAnswer(returnsFirstArg());

        ParkingSession result = parkingLotService.checkOut("REC-20260715080000-ABCD1234");

        assertNotNull(result);
        assertNotNull(result.getParkingFee());
        // 5 completed sessions -> no discount (not multiple of 10)
        assertEquals(BigDecimal.ZERO.setScale(2), result.getDiscount().setScale(2));
    }
}
