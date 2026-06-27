package com.geofleet.rider.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateRiderStatusRequest(
        @NotBlank String status
) {
}

