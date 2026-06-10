package com.caiohenrique.demo_park_api.web.controller;

import com.caiohenrique.demo_park_api.entity.ParkingSession;
import com.caiohenrique.demo_park_api.service.ParkingLotService;
import com.caiohenrique.demo_park_api.web.dto.ParkingSessionCreateDTO;
import com.caiohenrique.demo_park_api.web.dto.ParkingSessionResponseDTO;
import com.caiohenrique.demo_park_api.web.dto.mapper.ParkingSessionMapper;
import com.caiohenrique.demo_park_api.web.exception.ErrorMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
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


    @Operation(
            summary = "Operação de check-in", description = "Recurso para dar entrada em um veículo no estacionamento. " +
            "Requisição exige uso de um bearer token. Acesso restrito para ADMIN.",
            security = @SecurityRequirement(name = "security"),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Recurso criado com sucesso.",
                            headers = @Header(name = HttpHeaders.LOCATION, description = "URL de acesso ao recurso criado"),
                            content = @Content(mediaType = "application/json;charset=UTF-8",
                                    schema = @Schema(implementation = ParkingSessionResponseDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Causas possíveis: <br/>" +
                            "- CPF do cliente não cadastrado no sistema <br/>" +
                            "- Nenhuma vaga livre foi localizada",
                            content = @Content(mediaType = "application/json;charset=UTF-8",
                                    schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "422", description = "Recurso não processado por falta de dados ou dados inválidos",
                            content = @Content(mediaType = "application/json;charset=UTF-8",
                                    schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "403", description = "Recurso não permitido ao perfil de CLIENTE",
                            content = @Content(mediaType = "application/json;charset=UTF-8",
                                    schema = @Schema(implementation = ErrorMessage.class))),
            }
    )
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
