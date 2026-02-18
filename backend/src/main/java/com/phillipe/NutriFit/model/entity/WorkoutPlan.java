package com.phillipe.NutriFit.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "workout_plan")
public class WorkoutPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String name;

    private String description;

    @Builder.Default
    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    @Builder.Default
    @OneToMany(mappedBy = "workoutPlan", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<WorkoutPlanDay> days = new HashSet<>();

    public void addDay(WorkoutPlanDay day) {
        days.add(day);
        day.setWorkoutPlan(this);
    }

    public void removeDay(WorkoutPlanDay day) {
        days.remove(day);
        day.setWorkoutPlan(null);
    }
}
