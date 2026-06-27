package com.geofleet.rider.dto;

import com.geofleet.rider.entity.RiderStatus;
import com.geofleet.rider.entity.VehicleType;
import java.time.Instant;

public record RiderResponse(
        Long id,
        String name,
        String phoneNumber,
        String city,
        VehicleType vehicleType,
        RiderStatus riderStatus,
        Instant createdAt,
        Instant updatedAt
) {
}
