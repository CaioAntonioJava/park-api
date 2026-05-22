package com.caiohenrique.demo_park_api.web.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.br.CPF;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ParkingSessionCreateDTO {

    @NotBlank(message = "A placa é obrigatória")
    @Size(min = 8, max = 8)
    @Pattern(regexp = "([A-Z]{3}-\\d{4})|([A-Z]{3}\\d[A-Z]\\d{2})", message = "Placa inválida")
    private String licensePlate;

    @NotBlank(message = "A marca é obrigatória")
    private String brand;

    @NotBlank(message = "O modelo é obrigatório")
    private String model;

    @NotBlank(message = "A cor é obrigatória")
    private String color;

    @NotBlank(message = "O CPF é obrigatório")
    @CPF(message = "CPF inválido")
    private String clientCpf;

}
