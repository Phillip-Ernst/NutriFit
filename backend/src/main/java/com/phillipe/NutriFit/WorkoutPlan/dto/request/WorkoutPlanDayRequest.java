package com.phillipe.NutriFit.WorkoutPlan.dto.request;

import jakarta.validation.Valid;
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
    private Integer dayNumber;

    @NotBlank(message = "day name is required")
    private String dayName;

    private List<@Valid WorkoutPlanExerciseRequest> exercises;
}
