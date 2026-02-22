package com.caiohenrique.demo_park_api.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserChangePasswordDTO {

    @NotBlank
    @Size( min = 6, max = 6)
    @Schema(example = "123456")
    private String currentPassword;

    @NotBlank
    @Size( min = 6, max = 6)
    @Schema(example = "NovaSenha@123")
    private String newPassword;

    @NotBlank
    @Size( min = 6, max = 6)
    @Schema(example = "NovaSenha@123")
    private String confirmPassword;
}
