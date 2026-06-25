package com.caiohenrique.demo_park_api.web.dto.mapper;

import com.caiohenrique.demo_park_api.entity.ParkingSpot;
import com.caiohenrique.demo_park_api.web.dto.ParkingSpotCreateDTO;
import com.caiohenrique.demo_park_api.web.dto.ParkingSpotResponseDTO;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SpotMapper {

    // Instância única e compartilhada: a criação de ModelMapper é custosa
    // (faz scanning/reflection), então não deve ser refeita a cada requisição.
    private static final ModelMapper MAPPER = new ModelMapper();

    public static ParkingSpot parkingSpot(ParkingSpotCreateDTO parkingSpotCreateDTO) {
        return MAPPER.map(parkingSpotCreateDTO, ParkingSpot.class);
    }

    public static ParkingSpotResponseDTO parkingSpotResponseDTO(ParkingSpot parkingSpot) {
        return MAPPER.map(parkingSpot, ParkingSpotResponseDTO.class);
    }
}
