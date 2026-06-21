package com.caiohenrique.demo_park_api.web.controller;

import com.caiohenrique.demo_park_api.entity.ParkingSession;
import com.caiohenrique.demo_park_api.jwt.JwtUserDetails;
import com.caiohenrique.demo_park_api.repository.projection.ParkingSessionProjection;
import com.caiohenrique.demo_park_api.service.ParkingLotService;
import com.caiohenrique.demo_park_api.service.ParkingSessionService;
import com.caiohenrique.demo_park_api.web.dto.ParkingSessionCreateDTO;
import com.caiohenrique.demo_park_api.web.dto.ParkingSessionResponseDTO;
import com.caiohenrique.demo_park_api.web.dto.mapper.ParkingSessionMapper;
import com.caiohenrique.demo_park_api.web.exception.ErrorMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@Tag(name = "Sessões de Estacionamento", description = "Operações de entrada e saída de veículos no estacionamento")
@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/parking-sessions")
public class ParkingSessionController {

    private final ParkingLotService parkingLotService;
    private final ParkingSessionService parkingSessionService;


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

    @Operation(
            summary = "Buscar sessão de estacionamento por recibo", description = """
            Retorna os dados de uma sessão de estacionamento aberta
            a partir do número do recibo gerado no check-in.
            A requisição exige autenticação via Bearer Token.
            """,
            security = @SecurityRequirement(name = "security"),
            parameters = {
                    @Parameter(in = ParameterIn.PATH, name = "receipt", description = "Número do recibo gerado pelo check-in")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Recurso recuperado com sucesso.",
                            content = @Content(mediaType = "application/json;charset=UTF-8",
                                    schema = @Schema(implementation = ParkingSessionResponseDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Número do recibo não encontrado.",
                            content = @Content(mediaType = "application/json;charset=UTF-8",
                                    schema = @Schema(implementation = ErrorMessage.class))),
            }
    )
    @GetMapping("/{receipt}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENT')")
    public ResponseEntity<ParkingSessionResponseDTO> getOpenSessionByReceipt(@PathVariable String receipt) {

        ParkingSession openSession = parkingSessionService.findOpenSessionByReceipt(receipt);
        ParkingSessionResponseDTO responseDTO = ParkingSessionMapper.parkingSessionResponseDTO(openSession);

        return ResponseEntity.ok(responseDTO);
    }

    @Operation(
            summary = "Realizar check-out de veículo",
            description = "Finaliza a sessão de estacionamento de um veículo com base no número do recibo. " +
                    "Essa operação calcula o valor total, libera a vaga e encerra a sessão ativa. " +
                    "Requer autenticação via Bearer Token. Acesso restrito a usuários com perfil ADMIN.",
            security = @SecurityRequirement(name = "security"),
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "Check-out realizado com sucesso",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ParkingSessionResponseDTO.class))),
                    @ApiResponse(
                            responseCode = "401", description = "Não autenticado ou token inválido",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(
                            responseCode = "403", description = "Acesso negado: usuário não possui permissão",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(
                            responseCode = "404", description = "Recibo não encontrado ou sessão já finalizada",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorMessage.class)))
            }
    )
    @PutMapping("/check-out/{receipt}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ParkingSessionResponseDTO> checkOut(@PathVariable String receipt) {

        ParkingSession openSession = parkingLotService.checkOut(receipt);
        ParkingSessionResponseDTO responseDTO = ParkingSessionMapper.parkingSessionResponseDTO(openSession);

        return ResponseEntity.ok(responseDTO);
    }

    @Operation(
            summary = "Listar histórico de estacionamentos por CPF",
            description = "Recurso para consultar o histórico de sessões de estacionamento de um cliente " +
                    "por meio do CPF. A requisição exige uso de um bearer token válido. " +
                    "Acesso restrito a usuários com perfil ADMIN. " +
                    "O resultado é retornado de forma paginada.",

            security = @SecurityRequirement(name = "security"),

            parameters = {
                    @Parameter(
                            name = "cpf",
                            description = "CPF do cliente utilizado para consulta do histórico de estacionamentos",
                            example = "42212125860",
                            required = true
                    ),
                    @Parameter(
                            name = "page",
                            description = "Número da página a ser consultada",
                            example = "0"
                    ),
                    @Parameter(
                            name = "size",
                            description = "Quantidade de registros por página",
                            example = "5"
                    ),
                    @Parameter(
                            name = "sort",
                            description = "Critério de ordenação no formato campo,direção. Exemplo: checkIn,asc",
                            example = "checkIn,asc"
                    )
            },

            responses = {
                    @ApiResponse(responseCode = "200", description = "Histórico de estacionamentos retornado com sucesso",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ParkingSessionProjection.class))),
                    @ApiResponse(
                            responseCode = "404", description = "CPF informado é inválido, ou não cadastrado",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorMessage.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403", description = "Acesso negado. Recurso disponível apenas para administradores",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorMessage.class)))
            }
    )
    @GetMapping("/cpf/{cpf}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<ParkingSessionProjection>> getParkingSessionsByCpf(
            @PathVariable String cpf, @Parameter(hidden = true) @PageableDefault(size = 5, sort = "checkIn", direction = Sort.Direction.ASC) Pageable pageable) {

        Page<ParkingSessionProjection> projection = parkingSessionService.getAllParkingSessionsByCpf(cpf, pageable);
        return ResponseEntity.ok(projection);
    }

    @Operation(
            summary = "Listar todas as sessões de estacionamento do cliente autenticado",
            description = "Recurso para consultar todas as sessões de estacionamento associadas ao cliente autenticado. " +
                    "A requisição exige uso de um bearer token válido. " +
                    "Acesso restrito a usuários com perfil CLIENT. " +
                    "O resultado é retornado de forma paginada.",

            security = @SecurityRequirement(name = "security"),

            parameters = {
                    @Parameter(
                            name = "page",
                            description = "Número da página a ser consultada",
                            example = "0"
                    ),
                    @Parameter(
                            name = "size",
                            description = "Quantidade de registros por página",
                            example = "5"
                    ),
                    @Parameter(
                            name = "sort",
                            description = "Critério de ordenação no formato campo,direção. Exemplo: checkIn,asc",
                            example = "checkIn,asc"
                    )
            },

            responses = {
                    @ApiResponse(responseCode = "200", description = "Sessões de estacionamento retornadas com sucesso",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ParkingSessionProjection.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403", description = "Acesso negado. Recurso disponível apenas para clientes",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorMessage.class)))
            }
    )
    @GetMapping()
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<Page<ParkingSessionProjection>> getAllUserParkingSessions(@AuthenticationPrincipal JwtUserDetails user, @Parameter(hidden = true)
    @PageableDefault(size = 5, sort = "checkIn", direction = Sort.Direction.ASC)
    Pageable pageable) {
        Page<ParkingSessionProjection> projection = parkingSessionService.findAllByUserId(user.getId(), pageable);
        return ResponseEntity.ok(projection);
    }
}