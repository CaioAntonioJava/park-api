package com.caiohenrique.demo_park_api.web.controller;

import com.caiohenrique.demo_park_api.entity.Client;
import com.caiohenrique.demo_park_api.jwt.JwtUserDetails;
import com.caiohenrique.demo_park_api.repository.projection.ClientProjection;
import com.caiohenrique.demo_park_api.service.ClientService;
import com.caiohenrique.demo_park_api.service.UserService;
import com.caiohenrique.demo_park_api.web.dto.ClientCreateDTO;
import com.caiohenrique.demo_park_api.web.dto.ClientResponseDTO;
import com.caiohenrique.demo_park_api.web.dto.PageableDTO;
import com.caiohenrique.demo_park_api.web.dto.UserResponseDTO;
import com.caiohenrique.demo_park_api.web.dto.mapper.ClientMapper;
import com.caiohenrique.demo_park_api.web.dto.mapper.PageableMapper;
import com.caiohenrique.demo_park_api.web.exception.ErrorMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Clientes", description = "Contém todos as operaçõe relativas aos recursos para cadastro, edição e leitura de um cliente.")
@RestController
@RequestMapping("api/v1/clients")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;
    private final UserService userService;
    
    @Operation(
            summary = "Criar um novo cliente", description = "Recurso para criar um novo cliente vinculado a um usuário cadastrado. " +
            "Requisição exige uso de um bearer token. Acesso restrito para CLIENT.",
            security = @SecurityRequirement(name = "security"),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Recurso criado com sucesso.", content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserResponseDTO.class))),
                    @ApiResponse(responseCode = "409", description = "Cliente CPF já possuí cadastro no sistema.", content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "422", description = "Recurso não processado por falta de dados, ou dados inválidos.", content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "403", description = "Recurso não permitido ao perfil ADMIN.", content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class)))
            }
    )
    @PostMapping
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<ClientResponseDTO> create(@RequestBody @Valid ClientCreateDTO clientCreateDTO,
                                                    @AuthenticationPrincipal JwtUserDetails userDetails) {

        Client client = ClientMapper.toClient(clientCreateDTO);

        /*
         Recupera o usuário autenticado da requisição.
         O Spring Security extrai o usuário do JWT enviado no header Authorization,
         armazena no SecurityContext e injeta automaticamente aqui através de
         @AuthenticationPrincipal. Com o id do usuário autenticado, buscamos a
         entidade User no banco e associamos ao Client que está sendo criado.
         */
        client.setUser(userService.findById(userDetails.getId()));

        clientService.save(client);

        return ResponseEntity.status(201).body(ClientMapper.clientResponseDTO(client));
    }

    @Operation(
            summary = "Recuperar um cliente pelo id", description = "Requer autenticação via Bearer Token. Acesso permitido apenas para ADMIN e CLIENT.",
            security = @SecurityRequirement(name = "security"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Recurso recuperado com sucesso.", content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserResponseDTO.class))),
                    @ApiResponse(responseCode = "403", description = "Acesso negado: usuário não possui permissão para este recurso.", content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "404", description = "Recurso não encontrado.", content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class)))
            }
    )
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ClientResponseDTO> getById(@PathVariable Long id) {
        Client client = clientService.findById(id);
        return ResponseEntity.ok().body(ClientMapper.clientResponseDTO(client));
    }

    @Operation(
            summary = "Recuperar lista de clientes", description = "Requer autenticação via Bearer Token. Acesso permitido apenas para ADMIN.",
            security = @SecurityRequirement(name = "security"),
            parameters = {
                    @Parameter(in = ParameterIn.QUERY, name = "page",
                            content = @Content(schema = @Schema(type = "integer", defaultValue = "0")),
                            description = "Representa a página retornada."
                    ),
                    @Parameter(in = ParameterIn.QUERY, name = "size",
                            content = @Content(schema = @Schema(type = "integer", defaultValue = "20")),
                            description = "Representa o total de elementos na página."
                    ),
                    @Parameter(in = ParameterIn.QUERY, name = "sort", hidden = true,
                            content = @Content(schema = @Schema(type = "string", defaultValue = "id,asc")),
                            description = "Representa a ordenação dos resultados. Aceita multiplos critérios de ordenação suportados."
                    ),
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Recurso recuperado com sucesso", content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserResponseDTO.class))),
                    @ApiResponse(responseCode = "403", description = "Acesso negado: usuário não possui permissão para este recurso.", content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class)))
            }
    )
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PageableDTO> getAll(@Parameter(hidden = true) @PageableDefault(size = 5, sort = {"name"}) Pageable pageable) {
        Page<ClientProjection> clientList = clientService.findAll(pageable);
        return ResponseEntity.ok().body(PageableMapper.toPageableDto(clientList));
    }

    @Operation(
            summary = "Recuperar dados do cliente autenticado", description = "Requer autenticação via Bearer Token. Acesso permitido apenas para CLIENT.",
            security = @SecurityRequirement(name = "security"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Recurso recuperado com sucesso.", content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserResponseDTO.class))),
                    @ApiResponse(responseCode = "403", description = "Recurso não permitido ao perfil de ADMIN.", content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class)))
            }
    )
    // ===== Retrieves the currently authenticated user by ID. =====
    @GetMapping("/details")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<ClientResponseDTO> getDetails(@AuthenticationPrincipal JwtUserDetails jwtUserDetails) {
        Client client = clientService.findByUserId(jwtUserDetails.getId());
        return ResponseEntity.ok().body(ClientMapper.clientResponseDTO(client));
    }
}
