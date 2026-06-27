package com.geofleet.rider.controller;

import com.geofleet.rider.dto.RegisterRiderRequest;
import com.geofleet.rider.dto.RiderLiveStateResponse;
import com.geofleet.rider.dto.RiderResponse;
import com.geofleet.rider.dto.UpdateRiderStatusRequest;
import com.geofleet.rider.service.RiderService;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
}
