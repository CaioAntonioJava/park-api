package com.caiohenrique.demo_park_api.web.dto.mapper;

import com.caiohenrique.demo_park_api.entity.ParkingSession;
import com.caiohenrique.demo_park_api.web.dto.ParkingSessionCreateDTO;
import com.caiohenrique.demo_park_api.web.dto.ParkingSessionResponseDTO;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ParkingSessionMapper {

    // Instância única e compartilhada: a criação de ModelMapper é custosa
    // (faz scanning/reflection), então não deve ser refeita a cada requisição.
    private static final ModelMapper MAPPER = new ModelMapper();

    public static ParkingSession toParkingSession(ParkingSessionCreateDTO parkingSessionCreateDTO) {
        return MAPPER.map(parkingSessionCreateDTO, ParkingSession.class);
    }

    public static ParkingSessionResponseDTO parkingSessionResponseDTO(ParkingSession parkingSession) {
        return MAPPER.map(parkingSession, ParkingSessionResponseDTO.class);
    }
}
