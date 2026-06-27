package com.geofleet.rider.service;

import com.geofleet.rider.dto.NearbyRiderResponse;
import com.geofleet.rider.dto.RegisterRiderRequest;
import com.geofleet.rider.dto.RiderLocationUpdateResponse;
import com.geofleet.rider.dto.RiderLiveStateResponse;
import com.geofleet.rider.dto.RiderResponse;
import com.geofleet.rider.dto.UpdateRiderLocationRequest;
import com.geofleet.rider.dto.UpdateRiderStatusRequest;
import com.geofleet.rider.entity.Rider;
import com.geofleet.rider.entity.RiderStatus;
import com.geofleet.rider.exception.BadRequestException;
import com.geofleet.rider.exception.ResourceNotFoundException;
import com.geofleet.rider.repository.RiderRepository;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

import lombok.RequiredArgsConstructor;
import org.springframework.data.geo.*;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.domain.geo.Metrics;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RiderService {

    private final RiderRepository riderRepository;
    private final StringRedisTemplate redisTemplate;

    private static final Duration RIDER_FRESHNESS_WINDOW = Duration.ofSeconds(60);

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

    @Transactional(readOnly = true)
    public RiderLocationUpdateResponse updateLocation(Long riderId, UpdateRiderLocationRequest request) {
        Rider rider = getRiderOrThrow(riderId);
        Instant now = Instant.now();
        String geoKey = ridersGeoKey(rider.getCity());
        String liveStateKey = riderLiveStateKey(riderId);

        redisTemplate.opsForGeo().add(
                geoKey,
                new Point(request.longitude(), request.latitude()),
                riderId.toString()
        );

        Object existingStatus = redisTemplate.opsForHash().get(liveStateKey, "status");
        Object existingCurrentOrderId = redisTemplate.opsForHash().get(liveStateKey, "currentOrderId");

        Map<String, String> liveStateUpdates = new HashMap<>();
        liveStateUpdates.put("riderId", riderId.toString());
        liveStateUpdates.put("city", rider.getCity());
        liveStateUpdates.put("lastSeen", now.toString());
        liveStateUpdates.put("lastLat", request.latitude().toString());
        liveStateUpdates.put("lastLng", request.longitude().toString());
        liveStateUpdates.put("status", existingStatus == null ? RiderStatus.OFFLINE.name() : existingStatus.toString());
        liveStateUpdates.put("currentOrderId", existingCurrentOrderId == null ? "" : existingCurrentOrderId.toString());

        redisTemplate.opsForHash().putAll(liveStateKey, liveStateUpdates);

        return new RiderLocationUpdateResponse(
                riderId,
                rider.getCity(),
                request.latitude(),
                request.longitude(),
                now
        );
    }

    public List<NearbyRiderResponse> findNearby(
            String city,
            Double latitude,
            Double longitude,
            Double radiusKm
    ) {
        String geoKey = ridersGeoKey(city.toUpperCase());

        GeoResults<RedisGeoCommands.GeoLocation<String>> results =
                redisTemplate.opsForGeo().radius(
                        geoKey,
                        new Circle(
                                new Point(longitude, latitude),
                                new Distance(radiusKm, Metrics.KILOMETERS)
                        ),
                        RedisGeoCommands.GeoRadiusCommandArgs
                                .newGeoRadiusArgs()
                                .includeDistance()
                                .sortAscending()
                                .limit(50)
                );

        if (results == null) {
            return List.of();
        }

        Instant now = Instant.now();

        List<NearbyRiderResponse> nearbyRiders = new ArrayList<>();

        for (GeoResult<RedisGeoCommands.GeoLocation<String>> result : results) {
            String riderIdValue = result.getContent().getName();
            Long riderId = Long.valueOf(riderIdValue);

            Map<Object, Object> state = redisTemplate.opsForHash().entries(riderLiveStateKey(riderId));

            if (state == null || state.isEmpty()) {
                continue;
            }

            Object statusValue = state.get("status");
            Object lastSeenValue = state.get("lastSeen");

            if (statusValue == null || lastSeenValue == null) {
                continue;
            }

            RiderStatus status = RiderStatus.valueOf(statusValue.toString());

            if (status != RiderStatus.AVAILABLE) {
                continue;
            }

            Instant lastSeen = Instant.parse(lastSeenValue.toString());

            boolean fresh = Duration.between(lastSeen, now).compareTo(RIDER_FRESHNESS_WINDOW) <= 0;

            if (!fresh) {
                continue;
            }

            double distanceKm = result.getDistance().getValue();

            nearbyRiders.add(new NearbyRiderResponse(
                    riderId,
                    city.toUpperCase(),
                    status,
                    distanceKm,
                    lastSeen
            ));
        }

        return nearbyRiders;
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

    private String ridersGeoKey(String city) {
        return "riders:geo:" + city.toUpperCase();
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
