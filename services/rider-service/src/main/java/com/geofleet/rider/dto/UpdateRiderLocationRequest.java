package com.geofleet.rider.dto;

import jakarta.validation.constraints.NotNull;

public record UpdateRiderLocationRequest(
        @NotNull Double latitude,
        @NotNull Double longitude
) {
}

