package com.caiohenrique.demo_park_api.repository;

import com.caiohenrique.demo_park_api.entity.ParkingSpot;
import com.caiohenrique.demo_park_api.enums.SpotStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class ParkingSpotRepositoryTest {

    @Autowired
    private ParkingSpotRepository parkingSpotRepository;

    @Test
    void save_shouldPersistParkingSpot() {
        ParkingSpot spot = new ParkingSpot();
        spot.setSpotCode("A001");
        spot.setSpotStatus(SpotStatus.LIVRE);

        ParkingSpot saved = parkingSpotRepository.save(spot);

        assertNotNull(saved);
        assertNotNull(saved.getId());
        assertEquals("A001", saved.getSpotCode());
        assertEquals(SpotStatus.LIVRE, saved.getSpotStatus());
    }

    @Test
    void findBySpotCode_shouldReturnSpot() {
        ParkingSpot spot = new ParkingSpot();
        spot.setSpotCode("B002");
        spot.setSpotStatus(SpotStatus.OCUPADA);
        parkingSpotRepository.save(spot);

        Optional<ParkingSpot> found = parkingSpotRepository.findBySpotCode("B002");

        assertTrue(found.isPresent());
        assertEquals("B002", found.get().getSpotCode());
        assertEquals(SpotStatus.OCUPADA, found.get().getSpotStatus());
    }

    @Test
    void findBySpotCode_withNonExistingCode_shouldReturnEmpty() {
        Optional<ParkingSpot> found = parkingSpotRepository.findBySpotCode("Z999");

        assertFalse(found.isPresent());
    }

    @Test
    void findFirstBySpotStatusOrderBySpotCodeAsc_withAvailableSpots_shouldReturnFirst() {
        ParkingSpot spot1 = new ParkingSpot();
        spot1.setSpotCode("A001");
        spot1.setSpotStatus(SpotStatus.LIVRE);
        parkingSpotRepository.save(spot1);

        ParkingSpot spot2 = new ParkingSpot();
        spot2.setSpotCode("B001");
        spot2.setSpotStatus(SpotStatus.LIVRE);
        parkingSpotRepository.save(spot2);

        Optional<ParkingSpot> found = parkingSpotRepository.findFirstBySpotStatusOrderBySpotCodeAsc(SpotStatus.LIVRE);

        assertTrue(found.isPresent());
        assertEquals("A001", found.get().getSpotCode());
    }

    @Test
    void findFirstBySpotStatusOrderBySpotCodeAsc_withNoAvailableSpots_shouldReturnEmpty() {
        ParkingSpot spot = new ParkingSpot();
        spot.setSpotCode("A001");
        spot.setSpotStatus(SpotStatus.OCUPADA);
        parkingSpotRepository.save(spot);

        Optional<ParkingSpot> found = parkingSpotRepository.findFirstBySpotStatusOrderBySpotCodeAsc(SpotStatus.LIVRE);

        assertFalse(found.isPresent());
    }

    @Test
    void findAll_shouldReturnAllSpots() {
        ParkingSpot spot1 = new ParkingSpot();
        spot1.setSpotCode("A001");
        spot1.setSpotStatus(SpotStatus.LIVRE);
        parkingSpotRepository.save(spot1);

        ParkingSpot spot2 = new ParkingSpot();
        spot2.setSpotCode("A002");
        spot2.setSpotStatus(SpotStatus.OCUPADA);
        parkingSpotRepository.save(spot2);

        Page<ParkingSpot> spots = parkingSpotRepository.findAll(PageRequest.of(0, 10));

        assertEquals(2, spots.getTotalElements());
    }

    @Test
    void save_withDuplicateSpotCode_shouldThrowException() {
        ParkingSpot spot1 = new ParkingSpot();
        spot1.setSpotCode("A001");
        spot1.setSpotStatus(SpotStatus.LIVRE);
        parkingSpotRepository.save(spot1);

        ParkingSpot spot2 = new ParkingSpot();
        spot2.setSpotCode("A001");
        spot2.setSpotStatus(SpotStatus.OCUPADA);

        assertThrows(Exception.class, () -> parkingSpotRepository.save(spot2));
    }
}
