package com.caiohenrique.demo_park_api.web.dto.mapper;

import com.caiohenrique.demo_park_api.entity.ParkingSpot;
import com.caiohenrique.demo_park_api.enums.SpotStatus;
import com.caiohenrique.demo_park_api.web.dto.ParkingSpotCreateDTO;
import com.caiohenrique.demo_park_api.web.dto.ParkingSpotResponseDTO;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SpotMapperTest {

    @Test
    void parkingSpot_shouldMapCreateDtoToEntity() {
        ParkingSpotCreateDTO dto = new ParkingSpotCreateDTO("A001", SpotStatus.LIVRE);

        ParkingSpot spot = SpotMapper.parkingSpot(dto);

        assertNotNull(spot);
        assertEquals("A001", spot.getSpotCode());
        assertEquals(SpotStatus.LIVRE, spot.getSpotStatus());
        assertNull(spot.getId());
    }

    @Test
    void parkingSpotResponseDTO_shouldMapEntityToDto() {
        ParkingSpot spot = new ParkingSpot();
        spot.setId(1L);
        spot.setSpotCode("B002");
        spot.setSpotStatus(SpotStatus.OCUPADA);

        ParkingSpotResponseDTO dto = SpotMapper.parkingSpotResponseDTO(spot);

        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals("B002", dto.getSpotCode());
        assertEquals(SpotStatus.OCUPADA, dto.getSpotStatus());
    }
}
