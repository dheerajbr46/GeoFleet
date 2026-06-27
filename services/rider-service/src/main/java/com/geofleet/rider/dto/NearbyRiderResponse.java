package com.geofleet.rider.dto;

import com.geofleet.rider.entity.RiderStatus;

import java.time.Instant;

public record NearbyRiderResponse(
        Long riderId,
        String city,
        RiderStatus status,
        Double distanceKm,
        Instant lastSeen
) {
}

