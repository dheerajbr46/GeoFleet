package com.geofleet.rider.service;

import com.geofleet.rider.dto.RegisterRiderRequest;
import com.geofleet.rider.dto.RiderResponse;
import com.geofleet.rider.entity.Rider;
import com.geofleet.rider.entity.RiderStatus;
import com.geofleet.rider.exception.BadRequestException;
import com.geofleet.rider.exception.ResourceNotFoundException;
import com.geofleet.rider.repository.RiderRepository;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RiderService {

    private final RiderRepository riderRepository;

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
