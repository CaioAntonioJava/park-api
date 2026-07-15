package com.caiohenrique.demo_park_api.service;

import com.caiohenrique.demo_park_api.entity.Client;
import com.caiohenrique.demo_park_api.entity.ParkingSession;
import com.caiohenrique.demo_park_api.exception.EntityNotFoundException;
import com.caiohenrique.demo_park_api.repository.ParkingSessionRepository;
import com.caiohenrique.demo_park_api.repository.projection.ParkingSessionProjection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ParkingSessionServiceTest {

    @Mock
    private ParkingSessionRepository parkingSessionRepository;

    @Mock
    private ClientService clientService;

    @InjectMocks
    private ParkingSessionService parkingSessionService;

    private ParkingSession parkingSession;
    private Client client;

    @BeforeEach
    void setUp() {
        client = new Client();
        client.setId(1L);
        client.setCpf("12345678909");

        parkingSession = new ParkingSession();
        parkingSession.setId(1L);
        parkingSession.setReceiptNumber("REC-20260715100000-ABCD1234");
        parkingSession.setLicensePlate("BRA1234");
        parkingSession.setClient(client);
    }

    @Test
    void save_shouldSaveAndReturn() {
        when(parkingSessionRepository.save(any(ParkingSession.class))).thenAnswer(returnsFirstArg());

        ParkingSession saved = parkingSessionService.save(parkingSession);

        assertNotNull(saved);
        assertEquals("REC-20260715100000-ABCD1234", saved.getReceiptNumber());
        verify(parkingSessionRepository).save(parkingSession);
    }

    @Test
    void findOpenSessionByReceipt_withExistingOpenSession_shouldReturnSession() {
        when(parkingSessionRepository.findByReceiptNumberAndCheckOutIsNull("REC-20260715100000-ABCD1234"))
                .thenReturn(Optional.of(parkingSession));

        ParkingSession found = parkingSessionService.findOpenSessionByReceipt("REC-20260715100000-ABCD1234");

        assertNotNull(found);
        assertEquals("REC-20260715100000-ABCD1234", found.getReceiptNumber());
    }

    @Test
    void findOpenSessionByReceipt_withNonExistingReceipt_shouldThrowEntityNotFoundException() {
        when(parkingSessionRepository.findByReceiptNumberAndCheckOutIsNull("INVALID"))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> parkingSessionService.findOpenSessionByReceipt("INVALID"));
    }

    @Test
    void countCompletedParkingSessions_shouldReturnCount() {
        when(parkingSessionRepository.countByClientCpfAndCheckOutIsNotNull("12345678909"))
                .thenReturn(5L);

        long count = parkingSessionService.countCompletedParkingSessions("12345678909");

        assertEquals(5L, count);
    }

    @Test
    void existsOpenSessionByLicensePlate_withActiveSession_shouldReturnTrue() {
        when(parkingSessionRepository.existsByLicensePlateAndCheckOutIsNull("BRA1234"))
                .thenReturn(true);

        boolean exists = parkingSessionService.existsOpenSessionByLicensePlate("BRA1234");

        assertTrue(exists);
    }

    @Test
    void existsOpenSessionByLicensePlate_withNoActiveSession_shouldReturnFalse() {
        when(parkingSessionRepository.existsByLicensePlateAndCheckOutIsNull("BRA1234"))
                .thenReturn(false);

        boolean exists = parkingSessionService.existsOpenSessionByLicensePlate("BRA1234");

        assertFalse(exists);
    }

    @Test
    void getAllParkingSessionsByCpf_withExistingClient_shouldReturnSessions() {
        Pageable pageable = Pageable.ofSize(10);
        ParkingSessionProjection projection = mock(ParkingSessionProjection.class);
        Page<ParkingSessionProjection> page = new PageImpl<>(List.of(projection));

        when(clientService.findByCpf("12345678909")).thenReturn(client);
        when(parkingSessionRepository.findAllByClientCpf("12345678909", pageable)).thenReturn(page);

        Page<ParkingSessionProjection> result = parkingSessionService.getAllParkingSessionsByCpf("12345678909", pageable);

        assertEquals(1, result.getContent().size());
        verify(clientService).findByCpf("12345678909");
    }

    @Test
    void getAllParkingSessionsByCpf_withNonExistingClient_shouldThrowEntityNotFoundException() {
        Pageable pageable = Pageable.ofSize(10);

        when(clientService.findByCpf("00000000000"))
                .thenThrow(new EntityNotFoundException("Cliente não encontrado"));

        assertThrows(EntityNotFoundException.class,
                () -> parkingSessionService.getAllParkingSessionsByCpf("00000000000", pageable));
    }

    @Test
    void findAllByUserId_shouldReturnSessions() {
        Pageable pageable = Pageable.ofSize(10);
        ParkingSessionProjection projection = mock(ParkingSessionProjection.class);
        Page<ParkingSessionProjection> page = new PageImpl<>(List.of(projection));

        when(parkingSessionRepository.findAllByClientUserId(1L, pageable)).thenReturn(page);

        Page<ParkingSessionProjection> result = parkingSessionService.findAllByUserId(1L, pageable);

        assertEquals(1, result.getContent().size());
    }
}
