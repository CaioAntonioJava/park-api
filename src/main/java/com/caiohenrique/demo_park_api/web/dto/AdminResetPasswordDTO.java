package com.caiohenrique.demo_park_api.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminResetPasswordDTO {
    @NotBlank
    @Size(min = 6)
    private String newPassword;

    @NotBlank
    @Size(min = 6)
    private String confirmPassword;
}
