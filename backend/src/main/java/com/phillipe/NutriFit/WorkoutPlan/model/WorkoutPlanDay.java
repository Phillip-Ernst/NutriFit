package com.phillipe.NutriFit.WorkoutPlan.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "workout_plan_day")
public class WorkoutPlanDay {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "workout_plan_id", nullable = false)
    private WorkoutPlan workoutPlan;

    @Column(nullable = false)
    private Integer dayNumber;

    @Column(nullable = false)
    private String dayName;

    @Builder.Default
    @ElementCollection
    @CollectionTable(name = "workout_plan_day_exercises", joinColumns = @JoinColumn(name = "workout_plan_day_id"))
    private List<WorkoutPlanExercise> exercises = new ArrayList<>();
}
