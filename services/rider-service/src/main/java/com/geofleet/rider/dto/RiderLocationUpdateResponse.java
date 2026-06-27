package com.geofleet.rider.dto;

import java.time.Instant;

public record RiderLocationUpdateResponse(
        Long riderId,
        String city,
        Double latitude,
        Double longitude,
        Instant updatedAt
) {
}

