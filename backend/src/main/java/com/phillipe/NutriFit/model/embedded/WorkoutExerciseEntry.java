package com.phillipe.NutriFit.model.embedded;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkoutExerciseEntry {

    @Column(nullable = false)
    private String name;

    private String category;

    private Integer durationMinutes;

    private Integer sets;

    private Integer reps;

    private Integer weight;

    private Integer caloriesBurned;
}
