package com.phillipe.NutriFit.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkoutLogFromPlanRequest {

    @NotNull(message = "workout plan day id is required")
    private Long workoutPlanDayId;

    @NotEmpty(message = "exercises list cannot be empty")
    private List<@Valid ExerciseItemRequest> exercises;
}
