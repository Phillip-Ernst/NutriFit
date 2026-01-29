package com.phillipe.NutriFit.WorkoutPlan.dto.response;

import com.phillipe.NutriFit.WorkoutPlan.model.ExerciseCategory;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PredefinedExerciseResponse {
    private String id;
    private String name;
    private ExerciseCategory category;
}
