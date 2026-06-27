package com.geofleet.rider.dto;

import com.geofleet.rider.entity.RiderStatus;
import java.time.Instant;

public record RiderLiveStateResponse(
        Long riderId,
        RiderStatus status,
        String city,
        Instant lastSeen,
        String currentOrderId
) {
}
