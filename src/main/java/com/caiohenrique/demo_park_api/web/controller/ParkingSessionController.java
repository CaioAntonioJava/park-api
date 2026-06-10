package com.caiohenrique.demo_park_api.web.controller;

import com.caiohenrique.demo_park_api.entity.ParkingSession;
import com.caiohenrique.demo_park_api.service.ParkingLotService;
import com.caiohenrique.demo_park_api.web.dto.ParkingSessionCreateDTO;
import com.caiohenrique.demo_park_api.web.dto.ParkingSessionResponseDTO;
import com.caiohenrique.demo_park_api.web.dto.mapper.ParkingSessionMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/parking-sessions")
public class ParkingSessionController {

    private final ParkingLotService parkingLotService;

    @PostMapping("check-in")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ParkingSessionResponseDTO> checkIn(@RequestBody @Valid ParkingSessionCreateDTO createDTO) {

        ParkingSession parkingSession = ParkingSessionMapper.toParkingSession(createDTO);
        parkingLotService.checkIn(parkingSession);

        ParkingSessionResponseDTO responseDTO = ParkingSessionMapper.parkingSessionResponseDTO(parkingSession);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequestUri().path("/{receipt}")
                .buildAndExpand(parkingSession.getReceiptNumber())
                .toUri();

        return ResponseEntity.created(location).body(responseDTO);
    }

}
