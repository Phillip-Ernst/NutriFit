package com.phillipe.NutriFit.dto.response;

import com.phillipe.NutriFit.model.ExerciseCategory;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkoutPlanExerciseResponse {
    private String name;
    private ExerciseCategory category;
    private Boolean isCustom;
    private Integer targetSets;
    private Integer targetReps;
    private Integer targetWeight;
}
