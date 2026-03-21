package com.caiohenrique.demo_park_api.web.dto;

import com.caiohenrique.demo_park_api.enums.SpotStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ParkingSpotResponseDTO {
    private Long id;
    private String spotCode;
    private SpotStatus spotStatus;
}
