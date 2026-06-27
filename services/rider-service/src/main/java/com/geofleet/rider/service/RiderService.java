package com.geofleet.rider.service;

import com.geofleet.rider.dto.NearbyRiderResponse;
import com.geofleet.rider.dto.RegisterRiderRequest;
import com.geofleet.rider.dto.RiderResponse;
import com.geofleet.rider.dto.UpdateRiderLocationRequest;
import com.geofleet.rider.dto.UpdateRiderStatusRequest;
import com.geofleet.rider.entity.Rider;
import com.geofleet.rider.exception.BadRequestException;
import com.geofleet.rider.exception.ResourceNotFoundException;
import com.geofleet.rider.repository.RiderRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RiderService {

    private final RiderRepository riderRepository;

    @Transactional
    public RiderResponse register(RegisterRiderRequest request) {
        if (riderRepository.existsByPhoneNumber(request.phoneNumber())) {
            throw new BadRequestException("Rider with this phone number already exists");
        }

        Rider rider = Rider.builder()
                .name(request.name())
                .phoneNumber(request.phoneNumber())
                .city(request.city())
                .vehicleType(request.vehicleType())
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
    public List<RiderResponse> list() {
        return riderRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public void updateStatus(Long riderId, UpdateRiderStatusRequest request) {
        // TODO: Dheeraj will manually implement rider status state transitions and Redis availability state.
    }

    public void updateLocation(Long riderId, UpdateRiderLocationRequest request) {
        // TODO: Dheeraj will manually implement Redis GEO + lastSeen freshness logic.
    }

    public List<NearbyRiderResponse> findNearby(String city, Double latitude, Double longitude, Double radiusKm) {
        // TODO: Dheeraj will manually implement Redis GEO search, freshness filtering, and scoring.
        return List.of();
    }

    private RiderResponse toResponse(Rider rider) {
        return new RiderResponse(
                rider.getId(),
                rider.getName(),
                rider.getPhoneNumber(),
                rider.getCity(),
                rider.getVehicleType(),
                rider.getCreatedAt(),
                rider.getUpdatedAt()
        );
    }
}
