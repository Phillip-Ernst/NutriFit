package com.phillipe.NutriFit.dto.response;

import com.phillipe.NutriFit.model.entity.BodyMeasurement;
import lombok.*;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MeasurementResponse {
    private Long id;
    private Instant recordedAt;

    // All values in metric (cm, kg) - frontend handles conversion for display
    private Double heightCm;
    private Double weightKg;
    private Double bodyFatPercent;

    // Upper body
    private Double neckCm;
    private Double shouldersCm;
    private Double chestCm;
    private Double bicepsCm;
    private Double forearmsCm;

    // Core
    private Double waistCm;
    private Double hipsCm;

    // Lower body
    private Double thighsCm;
    private Double calvesCm;

    private String notes;

    public static MeasurementResponse fromEntity(BodyMeasurement measurement) {
        return MeasurementResponse.builder()
                .id(measurement.getId())
                .recordedAt(measurement.getRecordedAt())
                .heightCm(measurement.getHeightCm())
                .weightKg(measurement.getWeightKg())
                .bodyFatPercent(measurement.getBodyFatPercent())
                .neckCm(measurement.getNeckCm())
                .shouldersCm(measurement.getShouldersCm())
                .chestCm(measurement.getChestCm())
                .bicepsCm(measurement.getBicepsCm())
                .forearmsCm(measurement.getForearmsCm())
                .waistCm(measurement.getWaistCm())
                .hipsCm(measurement.getHipsCm())
                .thighsCm(measurement.getThighsCm())
                .calvesCm(measurement.getCalvesCm())
                .notes(measurement.getNotes())
                .build();
    }
}
