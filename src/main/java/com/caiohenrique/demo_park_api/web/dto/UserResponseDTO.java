package com.caiohenrique.demo_park_api.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Schema(description = "Dados retornados com as informações do usuário")
public class UserResponseDTO {

    @Schema(
            description = "Identificador único do usuário",
            example = "1"
    )
    private Long id;

    @Schema(
            description = "Email utilizado para acesso do usuário",
            example = "usuario@email.com.br"
    )
    private String username;

    @Schema(
            description = "Perfil de acesso do usuário",
            example = "CLIENT"
    )
    private String role;

}
