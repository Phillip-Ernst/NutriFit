package com.phillipe.NutriFit.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MeasurementRequest {

    // All values expected in metric (cm, kg) - frontend handles conversion

    @Min(value = 50, message = "Height must be at least 50 cm")
    @Max(value = 300, message = "Height cannot exceed 300 cm")
    private Double heightCm;

    @Min(value = 20, message = "Weight must be at least 20 kg")
    @Max(value = 500, message = "Weight cannot exceed 500 kg")
    private Double weightKg;

    @Min(value = 1, message = "Body fat must be at least 1%")
    @Max(value = 70, message = "Body fat cannot exceed 70%")
    private Double bodyFatPercent;

    // Upper body
    @Min(value = 10, message = "Neck measurement must be at least 10 cm")
    @Max(value = 100, message = "Neck measurement cannot exceed 100 cm")
    private Double neckCm;

    @Min(value = 30, message = "Shoulders measurement must be at least 30 cm")
    @Max(value = 200, message = "Shoulders measurement cannot exceed 200 cm")
    private Double shouldersCm;

    @Min(value = 40, message = "Chest measurement must be at least 40 cm")
    @Max(value = 200, message = "Chest measurement cannot exceed 200 cm")
    private Double chestCm;

    @Min(value = 15, message = "Biceps measurement must be at least 15 cm")
    @Max(value = 80, message = "Biceps measurement cannot exceed 80 cm")
    private Double bicepsCm;

    @Min(value = 10, message = "Forearms measurement must be at least 10 cm")
    @Max(value = 60, message = "Forearms measurement cannot exceed 60 cm")
    private Double forearmsCm;

    // Core
    @Min(value = 40, message = "Waist measurement must be at least 40 cm")
    @Max(value = 200, message = "Waist measurement cannot exceed 200 cm")
    private Double waistCm;

    @Min(value = 50, message = "Hips measurement must be at least 50 cm")
    @Max(value = 200, message = "Hips measurement cannot exceed 200 cm")
    private Double hipsCm;

    // Lower body
    @Min(value = 20, message = "Thighs measurement must be at least 20 cm")
    @Max(value = 120, message = "Thighs measurement cannot exceed 120 cm")
    private Double thighsCm;

    @Min(value = 15, message = "Calves measurement must be at least 15 cm")
    @Max(value = 80, message = "Calves measurement cannot exceed 80 cm")
    private Double calvesCm;

    @Size(max = 500, message = "Notes cannot exceed 500 characters")
    private String notes;
}
