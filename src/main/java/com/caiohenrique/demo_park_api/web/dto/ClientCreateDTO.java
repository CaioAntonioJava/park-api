package com.caiohenrique.demo_park_api.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.br.CPF;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Dados necessários para cadastro de um novo cliente")
public class ClientCreateDTO {

    @NotBlank
    @Size(min = 5, max = 100)
    @Schema(
            description = "Nome completo do cliente",
            example = "João da Silva",
            minLength = 5,
            maxLength = 100,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String name;

    @CPF
    @Size(min = 11, max = 11)
    @NotBlank
    @Schema(
            description = "CPF do cliente conforme padrão brasileiro",
            example = "12345678909",
            pattern = "\\d{11}",
            minLength = 11,
            maxLength = 11,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String cpf;
}