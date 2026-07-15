package com.caiohenrique.demo_park_api.web.dto.mapper;

import com.caiohenrique.demo_park_api.entity.Client;
import com.caiohenrique.demo_park_api.entity.ParkingSession;
import com.caiohenrique.demo_park_api.entity.ParkingSpot;
import com.caiohenrique.demo_park_api.enums.SpotStatus;
import com.caiohenrique.demo_park_api.web.dto.ParkingSessionCreateDTO;
import com.caiohenrique.demo_park_api.web.dto.ParkingSessionResponseDTO;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ParkingSessionMapperTest {

    @Test
    void toParkingSession_shouldMapCreateDtoToEntity() {
        ParkingSessionCreateDTO dto = new ParkingSessionCreateDTO(
                "BRA1234", "Toyota", "Corolla", "Prata", "12345678909"
        );

        ParkingSession session = ParkingSessionMapper.toParkingSession(dto);

        assertNotNull(session);
        assertEquals("BRA1234", session.getLicensePlate());
        assertEquals("Toyota", session.getBrand());
        assertEquals("Corolla", session.getModel());
        assertEquals("Prata", session.getColor());
        assertNotNull(session.getClient());
        assertEquals("12345678909", session.getClient().getCpf());
        assertNull(session.getId());
        assertNull(session.getReceiptNumber());
        assertNull(session.getCheckIn());
        assertNull(session.getCheckOut());
    }

    @Test
    void parkingSessionResponseDTO_shouldMapEntityToDto() {
        Client client = new Client();
        client.setId(1L);
        client.setCpf("12345678909");

        ParkingSpot spot = new ParkingSpot();
        spot.setId(1L);
        spot.setSpotCode("A001");
        spot.setSpotStatus(SpotStatus.LIVRE);

        ParkingSession session = new ParkingSession();
        session.setId(1L);
        session.setReceiptNumber("REC-20260715100000-ABCD1234");
        session.setLicensePlate("BRA1234");
        session.setBrand("Toyota");
        session.setModel("Corolla");
        session.setColor("Prata");
        session.setCheckIn(LocalDateTime.of(2026, 7, 15, 10, 0));
        session.setCheckOut(LocalDateTime.of(2026, 7, 15, 12, 0));
        session.setParkingFee(BigDecimal.valueOf(18.00));
        session.setDiscount(BigDecimal.valueOf(5.40));
        session.setClient(client);
        session.setParkingSpot(spot);

        ParkingSessionResponseDTO dto = ParkingSessionMapper.parkingSessionResponseDTO(session);

        assertNotNull(dto);
        assertEquals("REC-20260715100000-ABCD1234", dto.getReceiptNumber());
        assertEquals("BRA1234", dto.getLicensePlate());
        assertEquals("Toyota", dto.getBrand());
        assertEquals("Corolla", dto.getModel());
        assertEquals("Prata", dto.getColor());
        assertEquals("12345678909", dto.getClientCpf());
        assertEquals("A001", dto.getSpotCode());
        assertNotNull(dto.getCheckIn());
        assertNotNull(dto.getCheckOut());
        assertEquals(BigDecimal.valueOf(18.00), dto.getParkingFee());
        assertEquals(BigDecimal.valueOf(5.40), dto.getDiscount());
    }
}
