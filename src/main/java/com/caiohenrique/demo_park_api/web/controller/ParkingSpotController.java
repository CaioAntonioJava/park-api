package com.caiohenrique.demo_park_api.web.controller;

import com.caiohenrique.demo_park_api.entity.ParkingSpot;
import com.caiohenrique.demo_park_api.service.ParkingSpotService;
import com.caiohenrique.demo_park_api.web.dto.ParkingSpotCreateDTO;
import com.caiohenrique.demo_park_api.web.dto.ParkingSpotResponseDTO;
import com.caiohenrique.demo_park_api.web.dto.mapper.SpotMapper;
import com.caiohenrique.demo_park_api.web.exception.ErrorMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Vagas", description = "Contém os recursos para cadastro e leitura de uma vaga.")
@RestController
@RequestMapping("api/v1/parking-spots")
@RequiredArgsConstructor
public class ParkingSpotController {
    private final ParkingSpotService parkingSpotService;

    @Operation(
            summary = "Criar uma nova vaga",
            description = "Cria uma nova vaga de estacionamento no sistema." +
                    "Requer autenticação via Bearer Token e acesso restrito ao perfil ADMIN.",
            security = @SecurityRequirement(name = "security"),
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Vaga criada com sucesso.",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ParkingSpotResponseDTO.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Requisição inválida. O campo 'status' aceita apenas os valores: [LIVRE, OCUPADA].",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorMessage.class))
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Não autorizado. É necessário estar autenticado para acessar este recurso.",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorMessage.class))
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Acesso negado. Usuário não possui permissão para este recurso.",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorMessage.class))
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Conflito: já existe uma vaga cadastrada com os dados informados.",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorMessage.class))
                    ),
                    @ApiResponse(
                            responseCode = "422",
                            description = "Dados inválidos: o campo 'spotCode' deve conter exatamente 4 caracteres.",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorMessage.class))
                    )
            }
    )
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ParkingSpotResponseDTO> create(@RequestBody @Valid ParkingSpotCreateDTO createDTO) {
        ParkingSpot parkingSpot = SpotMapper.parkingSpot(createDTO);
        parkingSpotService.save(parkingSpot);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(SpotMapper.parkingSpotResponseDTO(parkingSpot));
    }

    @Operation(
            summary = "Buscar vaga por código",
            description = "Retorna os dados de uma vaga de estacionamento a partir do seu código. " +
                    "Requer autenticação via Bearer Token e acesso restrito ao perfil ADMIN.",
            security = @SecurityRequirement(name = "security"),
            parameters = {
                    @Parameter(
                            name = "spotCode",
                            description = "Código único da vaga (deve conter exatamente 4 caracteres). Ex: A123",
                            example = "A123",
                            required = true
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Vaga encontrada com sucesso.",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ParkingSpotResponseDTO.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Requisição inválida. O parâmetro 'spotCode' deve conter exatamente 4 caracteres.",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorMessage.class))
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Não autorizado. É necessário estar autenticado para acessar este recurso.",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorMessage.class))
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Acesso negado. Usuário não possui permissão para este recurso.",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorMessage.class))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Vaga não encontrada para o código informado.",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorMessage.class))
                    )
            }
    )
    @GetMapping("/{spotCode}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ParkingSpotResponseDTO> getSpotByCode(@PathVariable String spotCode) {
        ParkingSpot parkingSpot = parkingSpotService.findByCode(spotCode);
        return ResponseEntity.ok().body(SpotMapper.parkingSpotResponseDTO(parkingSpot));
    }
}