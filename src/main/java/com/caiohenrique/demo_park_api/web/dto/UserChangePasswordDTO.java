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
@Schema(description = "Dados necessários para alteração da senha do usuário autenticado")
public class UserChangePasswordDTO {

    @NotBlank
    @Size(min = 6, max = 100)
    @Schema(
            description = "Senha atual do usuário",
            example = "123456",
            minLength = 6,
            maxLength = 100,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String currentPassword;

    @NotBlank
    @Size(min = 6, max = 100)
    @Schema(
            description = "Nova senha que será cadastrada",
            example = "NovaSenha@123",
            minLength = 6,
            maxLength = 100,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String newPassword;

    @NotBlank
    @Size(min = 6, max = 100)
    @Schema(
            description = "Confirmação da nova senha informada",
            example = "NovaSenha@123",
            minLength = 6,
            maxLength = 100,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String confirmPassword;
}
