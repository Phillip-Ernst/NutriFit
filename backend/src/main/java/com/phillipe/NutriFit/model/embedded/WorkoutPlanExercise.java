package com.phillipe.NutriFit.model.embedded;

import com.phillipe.NutriFit.model.ExerciseCategory;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkoutPlanExercise {

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    private ExerciseCategory category;

    @Builder.Default
    @Column(nullable = false)
    private Boolean isCustom = false;

    private Integer targetSets;

    private Integer targetReps;

    private Integer targetWeight;
}
