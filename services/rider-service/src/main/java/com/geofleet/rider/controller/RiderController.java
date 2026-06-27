package com.geofleet.rider.controller;

import com.geofleet.rider.dto.NearbyRiderResponse;
import com.geofleet.rider.dto.RegisterRiderRequest;
import com.geofleet.rider.dto.RiderResponse;
import com.geofleet.rider.dto.UpdateRiderLocationRequest;
import com.geofleet.rider.dto.UpdateRiderStatusRequest;
import com.geofleet.rider.service.RiderService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/riders")
@RequiredArgsConstructor
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

    @GetMapping
    public List<RiderResponse> list() {
        return riderService.list();
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Void> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateRiderStatusRequest request
    ) {
        riderService.updateStatus(id, request);
        return ResponseEntity.accepted().build();
    }

    @PutMapping("/{id}/location")
    public ResponseEntity<Void> updateLocation(
            @PathVariable Long id,
            @Valid @RequestBody UpdateRiderLocationRequest request
    ) {
        riderService.updateLocation(id, request);
        return ResponseEntity.accepted().build();
    }

    @GetMapping("/nearby")
    public List<NearbyRiderResponse> findNearby(
            @RequestParam String city,
            @RequestParam Double latitude,
            @RequestParam Double longitude,
            @RequestParam(defaultValue = "5") Double radiusKm
    ) {
        return riderService.findNearby(city, latitude, longitude, radiusKm);
    }
}
