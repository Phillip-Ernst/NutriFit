package com.phillipe.NutriFit.WorkoutPlan.dto.request;

import com.phillipe.NutriFit.WorkoutPlan.model.ExerciseCategory;
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

    private Integer targetSets;

    private Integer targetReps;

    private Integer targetWeight;
}
