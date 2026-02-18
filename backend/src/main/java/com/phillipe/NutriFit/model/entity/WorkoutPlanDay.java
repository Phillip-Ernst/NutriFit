package com.phillipe.NutriFit.model.entity;

import com.phillipe.NutriFit.model.embedded.WorkoutPlanExercise;
import jakarta.persistence.*;
import lombok.*;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "workout_plan_day")
public class WorkoutPlanDay {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "workout_plan_id", nullable = false)
    private WorkoutPlan workoutPlan;

    @Column(nullable = false)
    @EqualsAndHashCode.Include
    private Integer dayNumber;

    @Column(nullable = false)
    private String dayName;

    @Builder.Default
    @ElementCollection
    @CollectionTable(name = "workout_plan_day_exercises", joinColumns = @JoinColumn(name = "workout_plan_day_id"))
    private Set<WorkoutPlanExercise> exercises = new LinkedHashSet<>();
}
