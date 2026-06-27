package com.geofleet.rider.dto;

import com.geofleet.rider.entity.RiderStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateRiderStatusRequest(
        @NotNull(message = "status is required")
        RiderStatus status
) {
}
