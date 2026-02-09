package com.phillipe.NutriFit.controller;

import com.phillipe.NutriFit.dto.request.MeasurementRequest;
import com.phillipe.NutriFit.dto.response.MeasurementResponse;
import com.phillipe.NutriFit.service.MeasurementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/measurements")
@RequiredArgsConstructor
public class MeasurementController {

    private final MeasurementService measurementService;

    @PostMapping
    public MeasurementResponse createMeasurement(@Valid @RequestBody MeasurementRequest request,
                                                  Authentication authentication) {
        String username = authentication.getName();
        return measurementService.createMeasurement(request, username);
    }

    @GetMapping
    public List<MeasurementResponse> getMeasurements(Authentication authentication) {
        String username = authentication.getName();
        return measurementService.getMeasurements(username);
    }

    @GetMapping("/latest")
    public ResponseEntity<MeasurementResponse> getLatestMeasurement(Authentication authentication) {
        String username = authentication.getName();
        MeasurementResponse latest = measurementService.getLatestMeasurement(username);
        if (latest == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(latest);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMeasurement(@PathVariable Long id,
                                                   Authentication authentication) {
        String username = authentication.getName();
        measurementService.deleteMeasurement(id, username);
        return ResponseEntity.noContent().build();
    }
}
