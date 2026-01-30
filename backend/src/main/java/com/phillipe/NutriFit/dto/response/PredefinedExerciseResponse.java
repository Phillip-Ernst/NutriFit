package com.phillipe.NutriFit.dto.response;

import com.phillipe.NutriFit.model.ExerciseCategory;
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
