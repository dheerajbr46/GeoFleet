package com.geofleet.rider.service;

import com.geofleet.rider.dto.RegisterRiderRequest;
import com.geofleet.rider.dto.RiderLiveStateResponse;
import com.geofleet.rider.dto.RiderResponse;
import com.geofleet.rider.dto.UpdateRiderStatusRequest;
import com.geofleet.rider.entity.Rider;
import com.geofleet.rider.entity.RiderStatus;
import com.geofleet.rider.exception.BadRequestException;
import com.geofleet.rider.exception.ResourceNotFoundException;
import com.geofleet.rider.repository.RiderRepository;

import java.time.Instant;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RiderService {

    private final RiderRepository riderRepository;
    private final StringRedisTemplate redisTemplate;

    @Transactional
    public RiderResponse register(RegisterRiderRequest request) {
        String phoneNumber = request.phoneNumber().trim();
        String city = request.city().trim().toUpperCase(Locale.ROOT);

        if (riderRepository.existsByPhoneNumber(phoneNumber)) {
            throw new BadRequestException("Rider with this phone number already exists");
        }

        Rider rider = Rider.builder()
                .name(request.name().trim())
                .phoneNumber(phoneNumber)
                .city(city)
                .vehicleType(request.vehicleType())
                .riderStatus(RiderStatus.OFFLINE)
                .build();

        return toResponse(riderRepository.save(rider));
    }

    @Transactional(readOnly = true)
    public RiderResponse getById(Long id) {
        return riderRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Rider not found: " + id));
    }

    @Transactional(readOnly = true)
    public RiderLiveStateResponse updateLiveStatus(Long riderId, UpdateRiderStatusRequest request) {
        Rider rider = getRiderOrThrow(riderId);
        validateRiderControlledStatus(request.status());

        Instant now = Instant.now();

        String key = riderLiveStateKey(riderId);

        Map<String, String> liveState = new HashMap<>();
        liveState.put("riderId", riderId.toString());
        liveState.put("status", request.status().name());
        liveState.put("city", rider.getCity());
        liveState.put("lastSeen", now.toString());
        liveState.put("currentOrderId", "");

        redisTemplate.opsForHash().putAll(key, liveState);

        return new RiderLiveStateResponse(riderId, request.status(), rider.getCity(), now, null);
    }

    @Transactional(readOnly = true)
    public RiderLiveStateResponse getLiveState(Long riderId) {
        Rider rider = getRiderOrThrow(riderId);
        String key = riderLiveStateKey(rider.getId());

        Map<Object, Object> state = redisTemplate.opsForHash().entries(key);
        if (state == null || state.isEmpty()) {
            throw new ResourceNotFoundException("Live state not found for rider id: " + riderId);
        }

        String status = state.get("status").toString();
        String city = state.get("city").toString();
        String lastSeen = state.get("lastSeen").toString();

        Object currentOrderIdValue = state.get("currentOrderId");
        String currentOrderId = currentOrderIdValue == null ? "" : currentOrderIdValue.toString();

        return new RiderLiveStateResponse(
                riderId,
                RiderStatus.valueOf(status),
                city,
                Instant.parse(lastSeen),
                currentOrderId
        );
    }

    private Rider getRiderOrThrow(Long riderId) {
        return riderRepository.findById(riderId)
                .orElseThrow(() -> new ResourceNotFoundException("Rider not found: " + riderId));
    }

    private void validateRiderControlledStatus(RiderStatus status) {
        if (status != RiderStatus.AVAILABLE && status != RiderStatus.OFFLINE) {
            throw new BadRequestException("Rider can only set status to AVAILABLE or OFFLINE");
        }
    }

    private String riderLiveStateKey(Long riderId) {
        return "rider:" + riderId;
    }

    private RiderResponse toResponse(Rider rider) {
        return new RiderResponse(
                rider.getId(),
                rider.getName(),
                rider.getPhoneNumber(),
                rider.getCity(),
                rider.getVehicleType(),
                rider.getRiderStatus(),
                rider.getCreatedAt(),
                rider.getUpdatedAt()
        );
    }
}
