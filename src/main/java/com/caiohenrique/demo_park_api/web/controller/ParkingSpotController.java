package com.caiohenrique.demo_park_api.web.controller;

import com.caiohenrique.demo_park_api.entity.ParkingSpot;
import com.caiohenrique.demo_park_api.service.ParkingSpotService;
import com.caiohenrique.demo_park_api.web.dto.ParkingSpotCreateDTO;
import com.caiohenrique.demo_park_api.web.dto.ParkingSpotResponseDTO;
import com.caiohenrique.demo_park_api.web.dto.mapper.SpotMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/parking-spots")
@RequiredArgsConstructor
public class ParkingSpotController {
    private final ParkingSpotService parkingSpotService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ParkingSpotResponseDTO> create(@RequestBody @Valid ParkingSpotCreateDTO createDTO) {
        ParkingSpot parkingSpot = SpotMapper.parkingSpot(createDTO);
        parkingSpotService.save(parkingSpot);
        return ResponseEntity.ok().body(SpotMapper.parkingSpotResponseDTO(parkingSpot));
    }

    @GetMapping("/{spotCode}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ParkingSpotResponseDTO> getSpotByCode(@PathVariable String spotCode) {
        ParkingSpot parkingSpot = parkingSpotService.findByCode(spotCode);
        return ResponseEntity.ok().body(SpotMapper.parkingSpotResponseDTO(parkingSpot));
    }
}
