package com.caiohenrique.demo_park_api.web.dto.mapper;

import com.caiohenrique.demo_park_api.entity.ParkingSession;
import com.caiohenrique.demo_park_api.web.dto.ParkingSessionCreateDTO;
import com.caiohenrique.demo_park_api.web.dto.ParkingSessionResponseDTO;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ParkingSessionMapper {
    public static ParkingSession toParkingSession(ParkingSessionCreateDTO parkingSessionCreateDTO) {
        return new ModelMapper().map(parkingSessionCreateDTO, ParkingSession.class);
    }

    public static ParkingSessionResponseDTO parkingSessionResponseDTO(ParkingSession parkingSession) {
        return new ModelMapper().map(parkingSession, ParkingSessionResponseDTO.class);
    }
}
