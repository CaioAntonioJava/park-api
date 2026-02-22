package com.caiohenrique.demo_park_api.web.controller;

import com.caiohenrique.demo_park_api.entity.User;
import com.caiohenrique.demo_park_api.service.UserService;
import com.caiohenrique.demo_park_api.web.dto.UserCreateDTO;
import com.caiohenrique.demo_park_api.web.dto.UserChangePasswordDTO;
import com.caiohenrique.demo_park_api.web.dto.UserResponseDTO;
import com.caiohenrique.demo_park_api.web.dto.mapper.UserMapper;
import com.caiohenrique.demo_park_api.web.exception.ErrorMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<UserResponseDTO> create (@Valid @RequestBody UserCreateDTO createDto) {
        User user = userService.save(UserMapper.toUser(createDto));
        return ResponseEntity.status(HttpStatus.CREATED).body(UserMapper.toResponseDto(user));
    }

    @Operation(
            summary = "Recuperar um usuário pelo id", description = "Recurso para recuperar um usuário pelo id",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Recurso recuperado com sucesso", content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserResponseDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Recurso não encontrado", content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class)))
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> findById (@PathVariable Long id) {
       User user = userService.findById(id);
        return ResponseEntity.ok().body(UserMapper.toResponseDto(user));
    }

    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> findAll () {
        List<User> users = userService.findAll();
        return ResponseEntity.ok().body(UserMapper.toListDto(users));
    }


    @Operation(
            summary = "Atualizar senha", description = "Recurso para atualizar senha de um usuário cadastrado",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Senha atualizada com sucesso", content = @Content(mediaType = "application/json",
                            schema = @Schema())),
                    @ApiResponse(responseCode = "404", description = "Recurso não encontrado", content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "400", description = "Erro de validação: " +
                            "a senha atual não corresponde ou a nova senha e a confirmação não correspondem", content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class)))
            }
    )
    @PatchMapping("/{id}")
    public ResponseEntity<Void> updatePassword (@PathVariable Long id, @Valid @RequestBody UserChangePasswordDTO userChangePasswordDTO) {
        User user =  userService.updatePassword(
                id, userChangePasswordDTO.getCurrentPassword(), userChangePasswordDTO.getNewPassword(), userChangePasswordDTO.getConfirmPassword()
        );
        return ResponseEntity.noContent().build();
    }
}
