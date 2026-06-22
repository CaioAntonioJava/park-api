package com.caiohenrique.demo_park_api.web.controller;

import com.caiohenrique.demo_park_api.entity.User;
import com.caiohenrique.demo_park_api.service.UserService;
import com.caiohenrique.demo_park_api.web.dto.AdminResetPasswordDTO;
import com.caiohenrique.demo_park_api.web.dto.UserChangePasswordDTO;
import com.caiohenrique.demo_park_api.web.dto.UserCreateDTO;
import com.caiohenrique.demo_park_api.web.dto.UserResponseDTO;
import com.caiohenrique.demo_park_api.web.dto.mapper.UserMapper;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Usuários", description = "Contém todos as operaçõe relativas aos recursos para cadastro, edição e leitura de um usuário")
@RestController
@RequestMapping("api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;


    @Operation(
            summary = "Criar um novo usuário", description = "Recurso para criar um novo usuário",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Recurso criado com sucesso", content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserResponseDTO.class))),
                    @ApiResponse(responseCode = "409", description = "Usuário email já cadastrado no sistema", content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "422", description = "Recurso não processado por dados de entrada inválidos", content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class)))
            }
    )
    @PostMapping
    public ResponseEntity<UserResponseDTO> create(@Valid @RequestBody UserCreateDTO createDto) {
        User user = userService.save(UserMapper.toUser(createDto));
        return ResponseEntity.status(HttpStatus.CREATED).body(UserMapper.toResponseDto(user));
    }

    @Operation(
            summary = "Recuperar um usuário pelo id", description = "Requer autenticação via Bearer Token. Acesso permitido apenas para ADMIN e CLIENT.",
            security = @SecurityRequirement(name = "security"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Recurso recuperado com sucesso", content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserResponseDTO.class))),
                    @ApiResponse(responseCode = "403", description = "Acesso negado: usuário não possui permissão para este recurso.", content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "404", description = "Recurso não encontrado", content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class)))
            }
    )
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or ( hasRole('CLIENT') and #id == authentication.principal.id )")
    public ResponseEntity<UserResponseDTO> getById(@PathVariable Long id) {
        User user = userService.findById(id);
        return ResponseEntity.ok().body(UserMapper.toResponseDto(user));
    }

    @Operation(
            summary = "Recuperar todos os usuários cadastrados", description = "Requer autenticação via Bearer Token. Acesso permitido apenas para ADMIN.",
            security = @SecurityRequirement(name = "security"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Recurso recuperado com sucesso", content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserResponseDTO.class))),
                    @ApiResponse(responseCode = "403", description = "Acesso negado: usuário não possui permissão para este recurso.", content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class)))
            }
    )
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<UserResponseDTO>> getAll(@Parameter(hidden = true) @PageableDefault(size = 5, sort = {"id"}) Pageable pageable) {
        Page<User> users = userService.findAll(pageable);
        return ResponseEntity.ok(users.map(UserMapper::toResponseDto));
    }


    @Operation(
            summary = "Atualizar a própria senha", description = "Permite que o usuário autenticado altere sua própria senha. Requer autenticação via Bearer Token.",
            security = @SecurityRequirement(name = "security"),
            responses = {
                    @ApiResponse(responseCode = "204", description = "Senha atualizada com sucesso", content = @Content(mediaType = "application/json",
                            schema = @Schema())),
                    @ApiResponse(responseCode = "403", description = "Acesso negado: usuário não possui permissão para este recurso.", content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "404", description = "Recurso não encontrado", content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "400", description = "Erro de validação: " +
                            "a senha atual não corresponde ou a nova senha e a confirmação não correspondem", content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class)))
            }
    )
    @PatchMapping("/{id}/password")
    @PreAuthorize("#id == authentication.principal.id")
    public ResponseEntity<Void> changePassword(@PathVariable Long id, @Valid @RequestBody UserChangePasswordDTO userChangePasswordDTO) {
        User user = userService.changePassword(
                id, userChangePasswordDTO.getCurrentPassword(), userChangePasswordDTO.getNewPassword(), userChangePasswordDTO.getConfirmPassword()
        );
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Redefinir senha de um usuário", description = "Permite que um administrador redefina a senha de qualquer usuário cadastrado. Requer autenticação via Bearer Token.",
            security = @SecurityRequirement(name = "security"),
            responses = {
                    @ApiResponse(responseCode = "204", description = "Senha atualizada com sucesso"),
                    @ApiResponse(responseCode = "403", description = "Acesso negado: usuário não possui permissão para este recurso.", content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "404", description = "Recurso não encontrado", content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "400", description = "Erro de validação: senha inválida ou confirmação de senha divergente", content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class)))
            }
    )
    @PatchMapping("/{id}/reset-password")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> resetPassword(@PathVariable Long id, @Valid @RequestBody AdminResetPasswordDTO dto) {

        userService.resetPassword(id, dto.getNewPassword(), dto.getConfirmPassword());
        return ResponseEntity.noContent().build();
    }
}
