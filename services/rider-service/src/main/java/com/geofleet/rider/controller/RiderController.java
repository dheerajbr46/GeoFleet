package com.geofleet.rider.controller;

import com.geofleet.rider.dto.RegisterRiderRequest;
import com.geofleet.rider.dto.NearbyRiderResponse;
import com.geofleet.rider.dto.RiderLocationUpdateResponse;
import com.geofleet.rider.dto.RiderLiveStateResponse;
import com.geofleet.rider.dto.RiderResponse;
import com.geofleet.rider.dto.UpdateRiderLocationRequest;
import com.geofleet.rider.dto.UpdateRiderStatusRequest;
import com.geofleet.rider.service.RiderService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/riders")
@RequiredArgsConstructor
@Validated
public class RiderController {

    private final RiderService riderService;

    @PostMapping
    public ResponseEntity<RiderResponse> register(@Valid @RequestBody RegisterRiderRequest request) {
        RiderResponse response = riderService.register(request);
        return ResponseEntity
                .created(URI.create("/api/v1/riders/" + response.id()))
                .body(response);
    }

    @GetMapping("/{id}")
    public RiderResponse getById(@PathVariable Long id) {
        return riderService.getById(id);
    }

    @PatchMapping("/{riderId}/status")
    public ResponseEntity<RiderLiveStateResponse> updateStatus(
            @PathVariable Long riderId,
            @Valid @RequestBody UpdateRiderStatusRequest request
    ) {
        return ResponseEntity.accepted().body(riderService.updateLiveStatus(riderId, request));
    }

    @GetMapping("/{riderId}/live-state")
    public RiderLiveStateResponse getLiveState(@PathVariable Long riderId) {
        return riderService.getLiveState(riderId);
    }

    @PatchMapping("/{riderId}/location")
    public ResponseEntity<RiderLocationUpdateResponse> updateLocation(
            @PathVariable Long riderId,
            @Valid @RequestBody UpdateRiderLocationRequest request
    ) {
        return ResponseEntity.accepted().body(riderService.updateLocation(riderId, request));
    }

    @GetMapping("/nearby")
    public List<NearbyRiderResponse> findNearby(
            @RequestParam @NotBlank String city,
            @RequestParam @NotNull @DecimalMin("-90.0") @DecimalMax("90.0") Double latitude,
            @RequestParam @NotNull @DecimalMin("-180.0") @DecimalMax("180.0") Double longitude,
            @RequestParam(defaultValue = "5.0") @DecimalMin("0.1") Double radiusKm
    ) {
        return riderService.findNearby(city, latitude, longitude, radiusKm);
    }
}
