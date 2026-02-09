package com.phillipe.NutriFit.service;

import com.phillipe.NutriFit.dto.request.MeasurementRequest;
import com.phillipe.NutriFit.dto.response.MeasurementResponse;

import java.util.List;

public interface MeasurementService {
    MeasurementResponse createMeasurement(MeasurementRequest request, String username);
    List<MeasurementResponse> getMeasurements(String username);
    MeasurementResponse getLatestMeasurement(String username);
    void deleteMeasurement(Long id, String username);
}
