package com.phillipe.NutriFit.dto.request;

import com.phillipe.NutriFit.model.ExerciseCategory;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkoutPlanExerciseRequest {

    @NotBlank(message = "exercise name is required")
    private String name;

    private ExerciseCategory category;

    private Boolean isCustom;

    @Min(value = 0, message = "target sets must be non-negative")
    private Integer targetSets;

    @Min(value = 0, message = "target reps must be non-negative")
    private Integer targetReps;

    @Min(value = 0, message = "target weight must be non-negative")
    private Integer targetWeight;
}
