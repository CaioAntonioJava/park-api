package com.caiohenrique.demo_park_api.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserLoginDto {

    @Schema(description = "Email do usuário utilizado para login", example = "usuario@email.com")
    @NotBlank
    @Email(message = "Email inválido", regexp = "^[a-z0-9.+-]+@[a-z0-9.-]+\\.[a-z]{2,}$")
    private String username;

    @Schema(description = "Senha do usuário", example = "123456")
    @NotBlank
    @Size(min = 6, max = 6)
    private String password;
}
