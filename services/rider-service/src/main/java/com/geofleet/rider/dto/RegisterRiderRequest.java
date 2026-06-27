package com.geofleet.rider.dto;

import jakarta.validation.constraints.NotBlank;

public record RegisterRiderRequest(
        @NotBlank String name,
        @NotBlank String phoneNumber,
        @NotBlank String city,
        @NotBlank String vehicleType
) {
}

