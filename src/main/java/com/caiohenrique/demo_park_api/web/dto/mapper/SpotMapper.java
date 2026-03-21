package com.caiohenrique.demo_park_api.web.dto.mapper;

import com.caiohenrique.demo_park_api.entity.ParkingSpot;
import com.caiohenrique.demo_park_api.web.dto.ParkingSpotCreateDTO;
import com.caiohenrique.demo_park_api.web.dto.ParkingSpotResponseDTO;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SpotMapper {
    public static ParkingSpot parkingSpot(ParkingSpotCreateDTO parkingSpotCreateDTO) {
        return new ModelMapper().map(parkingSpotCreateDTO, ParkingSpot.class);
    }

    public static ParkingSpotResponseDTO parkingSpotResponseDTO(ParkingSpot parkingSpot) {
        return new ModelMapper().map(parkingSpot, ParkingSpotResponseDTO.class);
    }
}
