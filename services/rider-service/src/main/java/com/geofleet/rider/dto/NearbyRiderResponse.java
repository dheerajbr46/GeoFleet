package com.geofleet.rider.dto;

public record NearbyRiderResponse(
        Long riderId,
        String name,
        String vehicleType,
        Double distanceKm
) {
}

