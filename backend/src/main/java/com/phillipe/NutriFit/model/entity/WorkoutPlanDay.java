package com.phillipe.NutriFit.model.entity;

import com.phillipe.NutriFit.model.embedded.WorkoutPlanExercise;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

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

    // UUID for equals/hashCode before entity is persisted
    @Builder.Default
    @Column(nullable = false, updatable = false, unique = true)
    private String uuid = UUID.randomUUID().toString();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WorkoutPlanDay that = (WorkoutPlanDay) o;
        return Objects.equals(uuid, that.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }

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
