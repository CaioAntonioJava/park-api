package com.caiohenrique.demo_park_api.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Dados necessários para autenticação de um usuário")
public class UserLoginDTO {


    @NotBlank
    @Email(message = "Email inválido", regexp = "^[a-z0-9.+-]+@[a-z0-9.-]+\\.[a-z]{2,}$")
    @Schema(
            description = "Email utilizado para autenticação do usuário",
            example = "usuario@email.com.br",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String username;

    @NotBlank
    @Schema(
            description = "Senha utilizada para autenticação do usuário",
            example = "123456",
            minLength = 6,
            maxLength = 100,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String password;
}
