package com.phillipe.NutriFit.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkoutPlanDayRequest {

    @NotNull(message = "day number is required")
    @Min(value = 1, message = "day number must be at least 1")
    private Integer dayNumber;

    @NotBlank(message = "day name is required")
    private String dayName;

    private List<@Valid WorkoutPlanExerciseRequest> exercises;
}
