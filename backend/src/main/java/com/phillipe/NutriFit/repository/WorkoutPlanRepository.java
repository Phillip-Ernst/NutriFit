package com.phillipe.NutriFit.repository;

import com.phillipe.NutriFit.model.entity.WorkoutPlan;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WorkoutPlanRepository extends JpaRepository<WorkoutPlan, Long> {

    @EntityGraph(attributePaths = {"days", "days.exercises"})
    List<WorkoutPlan> findByUserIdOrderByCreatedAtDesc(Long userId);

    @EntityGraph(attributePaths = {"days", "days.exercises"})
    Optional<WorkoutPlan> findByIdAndUserId(Long id, Long userId);
}
