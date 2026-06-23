package com.caiohenrique.demo_park_api.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Dados necessários para redefinição de senha de usuário por administrador")
public class AdminResetPasswordDTO {
    @NotBlank
    @Size(min = 6, max = 100)
    @Schema(
            description = "Nova senha do usuário",
            example = "123456",
            minLength = 6,
            maxLength = 100,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String newPassword;

    @Schema(
            description = "Confirmação da nova senha informada",
            example = "123456",
            minLength = 6,
            maxLength = 100,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank
    @Size(min = 6, max = 100)
    private String confirmPassword;
}