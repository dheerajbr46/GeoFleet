package com.geofleet.rider.dto;

import java.time.Instant;

public record RiderResponse(
        Long id,
        String name,
        String phoneNumber,
        String city,
        String vehicleType,
        Instant createdAt,
        Instant updatedAt
) {
}

