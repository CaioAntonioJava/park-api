package com.caiohenrique.demo_park_api.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Dados necessários para cadastro de um novo usuário")
public class UserCreateDTO {

    @NotBlank
    @Email(message = "Email inválido", regexp = "^[a-z0-9.+-]+@[a-z0-9.-]+\\.[a-z]{2,}$")
    @Schema(
            description = "Email utilizado como identificador de acesso do usuário",
            example = "usuario@email.com.br",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String username;

    @NotBlank
    @Size(min = 6, max = 100)
    @Schema(
            description = "Senha de acesso do usuário",
            example = "123456",
            minLength = 6,
            maxLength = 100,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String password;
    
}
