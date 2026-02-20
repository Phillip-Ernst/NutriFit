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
public class WorkoutSetEntry {

    @Column(nullable = false)
    private Integer setNumber;

    private Integer reps;

    private Integer weight;

    @Builder.Default
    private Boolean completed = true;

    private String notes;
}
