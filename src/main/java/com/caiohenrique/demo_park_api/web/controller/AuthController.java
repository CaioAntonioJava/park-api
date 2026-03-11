package com.caiohenrique.demo_park_api.web.controller;

import com.caiohenrique.demo_park_api.jwt.JwtToken;
import com.caiohenrique.demo_park_api.service.AuthService;
import com.caiohenrique.demo_park_api.web.dto.UserLoginDto;
import com.caiohenrique.demo_park_api.web.dto.UserResponseDTO;
import com.caiohenrique.demo_park_api.web.exception.ErrorMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Autenticação", description = "Recurso responsável por processar a autenticação de usuários na API, validando credenciais e retornando o token de acesso.")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    private final AuthService authService;
    private final AuthenticationManager authenticationManager;


    @Operation(
            summary = "Autenticar usuário", description = "Endpoint responsável por realizar a autenticação do usuário na API." +
            " Ao informar credenciais válidas, um Bearer Token (JWT) é retornado para acesso aos demais recursos protegidos.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Autenticação realizada com sucesso. Retorna um Bearer Token para acesso à API.",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = UserResponseDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Credenciais inválidas", content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "422", description = "Erro de validação nos campos da requisição",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorMessage.class)))
            }
    )
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid UserLoginDto dto, HttpServletRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword()));
            JwtToken token = authService.authenticate(authentication);
            return ResponseEntity.ok(token);
        } catch (AuthenticationException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorMessage(request, HttpStatus.UNAUTHORIZED, "Credenciais inválidas."));
        }
    }
}
