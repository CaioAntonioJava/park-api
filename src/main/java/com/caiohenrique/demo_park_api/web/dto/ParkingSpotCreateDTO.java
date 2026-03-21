package com.caiohenrique.demo_park_api.web.dto;

import com.caiohenrique.demo_park_api.enums.SpotStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ParkingSpotCreateDTO {

    @NotBlank
    @Size(min = 4, max = 4)
    @Pattern(regexp = "^[A-Z0-9]{4}$")
    private String spotCode;

    @NotNull
    private SpotStatus spotStatus;
}
