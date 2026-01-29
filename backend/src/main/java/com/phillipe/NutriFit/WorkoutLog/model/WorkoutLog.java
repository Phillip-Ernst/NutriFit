package com.phillipe.NutriFit.WorkoutLog.model;

import com.phillipe.NutriFit.User.model.User;
import com.phillipe.NutriFit.WorkoutPlan.model.WorkoutPlanDay;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "workout_log")
public class WorkoutLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workout_plan_day_id")
    private WorkoutPlanDay workoutPlanDay;

    @Builder.Default
    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    @Builder.Default
    @Column(nullable = false)
    private Integer totalDurationMinutes = 0;

    @Builder.Default
    @Column(nullable = false)
    private Integer totalCaloriesBurned = 0;

    @Builder.Default
    @Column(nullable = false)
    private Integer totalSets = 0;

    @Builder.Default
    @Column(nullable = false)
    private Integer totalReps = 0;

    @Builder.Default
    @ElementCollection
    @CollectionTable(name = "workout_log_exercises", joinColumns = @JoinColumn(name = "workout_log_id"))
    private List<WorkoutExerciseEntry> exercises = new ArrayList<>();

}
