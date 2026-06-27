package com.geofleet.rider.dto;

import com.geofleet.rider.entity.VehicleType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRiderRequest(
        @NotBlank
        @Size(max = 100)
        String name,

        @NotBlank
        @Size(max = 20)
        @Pattern(regexp = "^[0-9+\\-() ]+$", message = "phoneNumber contains unsupported characters")
        String phoneNumber,

        @NotBlank
        @Size(max = 80)
        String city,

        @NotNull
        VehicleType vehicleType
) {
}
