package com.caiohenrique.demo_park_api.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserLoginDTO {

    @Schema(description = "Email do usuário utilizado para login", example = "usuario@email.com.br")
    @NotBlank
    @Email(message = "Email inválido", regexp = "^[a-z0-9.+-]+@[a-z0-9.-]+\\.[a-z]{2,}$")
    private String username;

    @Schema(description = "Senha do usuário", example = "123456")
    @NotBlank
    private String password;
}
