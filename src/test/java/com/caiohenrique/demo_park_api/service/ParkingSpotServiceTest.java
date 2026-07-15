package com.caiohenrique.demo_park_api.service;

import com.caiohenrique.demo_park_api.entity.ParkingSpot;
import com.caiohenrique.demo_park_api.enums.SpotStatus;
import com.caiohenrique.demo_park_api.exception.CodeUniqueViolationException;
import com.caiohenrique.demo_park_api.exception.EntityNotFoundException;
import com.caiohenrique.demo_park_api.repository.ParkingSpotRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
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
class ParkingSpotServiceTest {

    @Mock
    private ParkingSpotRepository parkingSpotRepository;

    @InjectMocks
    private ParkingSpotService parkingSpotService;

    private ParkingSpot parkingSpot;

    @BeforeEach
    void setUp() {
        parkingSpot = new ParkingSpot();
        parkingSpot.setId(1L);
        parkingSpot.setSpotCode("A001");
        parkingSpot.setSpotStatus(SpotStatus.LIVRE);
    }

    @Test
    void save_withValidSpot_shouldSaveAndReturn() {
        when(parkingSpotRepository.save(any(ParkingSpot.class))).thenAnswer(returnsFirstArg());

        ParkingSpot saved = parkingSpotService.save(parkingSpot);

        assertNotNull(saved);
        assertEquals("A001", saved.getSpotCode());
        assertEquals(SpotStatus.LIVRE, saved.getSpotStatus());
    }

    @Test
    void save_withDuplicateCode_shouldThrowCodeUniqueViolationException() {
        when(parkingSpotRepository.save(any(ParkingSpot.class)))
                .thenThrow(DataIntegrityViolationException.class);

        assertThrows(CodeUniqueViolationException.class,
                () -> parkingSpotService.save(parkingSpot));
    }

    @Test
    void findByCode_withExistingCode_shouldReturnSpot() {
        when(parkingSpotRepository.findBySpotCode("A001")).thenReturn(Optional.of(parkingSpot));

        ParkingSpot found = parkingSpotService.findByCode("A001");

        assertNotNull(found);
        assertEquals("A001", found.getSpotCode());
    }

    @Test
    void findByCode_withNonExistingCode_shouldThrowEntityNotFoundException() {
        when(parkingSpotRepository.findBySpotCode("Z999")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> parkingSpotService.findByCode("Z999"));
    }

    @Test
    void findAvailableParkingSpot_withAvailableSpot_shouldReturnSpot() {
        when(parkingSpotRepository.findFirstBySpotStatusOrderBySpotCodeAsc(SpotStatus.LIVRE))
                .thenReturn(Optional.of(parkingSpot));

        ParkingSpot found = parkingSpotService.findAvailableParkingSpot();

        assertNotNull(found);
        assertEquals("A001", found.getSpotCode());
        assertEquals(SpotStatus.LIVRE, found.getSpotStatus());
    }

    @Test
    void findAvailableParkingSpot_withNoAvailableSpot_shouldThrowEntityNotFoundException() {
        when(parkingSpotRepository.findFirstBySpotStatusOrderBySpotCodeAsc(SpotStatus.LIVRE))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> parkingSpotService.findAvailableParkingSpot());
    }

    @Test
    void findAll_shouldReturnPagedSpots() {
        Pageable pageable = Pageable.ofSize(10);
        Page<ParkingSpot> page = new PageImpl<>(List.of(parkingSpot));
        when(parkingSpotRepository.findAll(pageable)).thenReturn(page);

        Page<ParkingSpot> result = parkingSpotService.findAll(pageable);

        assertEquals(1, result.getContent().size());
        assertEquals("A001", result.getContent().get(0).getSpotCode());
    }
}
